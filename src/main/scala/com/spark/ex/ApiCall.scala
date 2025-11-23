package com.spark.ex

import org.apache.hadoop.shaded.org.apache.http.HttpClientConnection
import org.apache.spark.SparkConf

import java.net.HttpURLConnection
import java.net.URL
import org.apache.spark.sql.SparkSession

import java.util.Properties
import scala.io.Source

import org.json4s._
import org.json4s.jackson.JsonMethods._


object ApiCall {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .config(getSparkAppConf)
      .getOrCreate()

    val apiUrl = "https://jsonplaceholder.typicode.com/todos/1"

    val jsonResponse = makeApiCall(apiUrl)
    println(s"Response from JSONPlaceholder API:\n $jsonResponse")

    implicit val formats: DefaultFormats.type = DefaultFormats

    val prop = new Properties()
    //val details = """{"dateTimeFormat": "yyyy-MM-dd'T'HH:mm:ss", "timenow": 1701358730685, "timeZone": "GMT"}"""

    val json = parse(jsonResponse)

    val jsonMap = json.extract[Map[String, Any]]

    jsonMap.foreach {
      case (key, value) =>
        prop.setProperty(key, value.toString)
    }

    /*val jsonPairs = details
      .stripPrefix("{")
      .stripSuffix("}")
      .split(",")
      .map(_.trim)
      .map { pair =>
        val keyValue = pair.split(":")
        (keyValue(0).trim, keyValue(1).trim)
      }

    // Set properties
    jsonPairs.foreach { case (key, value) =>
      //prop.setProperty(key.stripPrefix("\"").stripSuffix("\""), value.stripPrefix("\"").stripSuffix("\""))
      prop.setProperty(key.stripPrefix("\"").stripSuffix("\""), value.stripPrefix("\"").stripSuffix("\""))
    }*/
    println(s"time zone is  :\n ${prop.getProperty("title")}")
    spark.stop()
  }

  def makeApiCall(apiUrl: String): String = {

    try{
      //HttpClientConnection
      val url = new URL(apiUrl)  //apiUrl = "https://jsonplaceholder.typicode.com/todos/1"
      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")

      val responseCode = connection.getResponseCode
      if (responseCode == HttpURLConnection.HTTP_OK) {
        val inputStream = connection.getInputStream
        val response = scala.io.Source.fromInputStream(inputStream).mkString
        inputStream.close()
        response
      } else {
        throw new RuntimeException(s"Error: $responseCode")
      }

    } catch {
      case e: Exception =>
        throw new RuntimeException(s"Exception: ${e.getMessage}")
    }

  }

  def getSparkAppConf: SparkConf = {

    val sparkAppConf = new SparkConf

    val props = new Properties
    props.load(Source.fromFile("spark.conf").bufferedReader())
    props.forEach((k, v) => sparkAppConf.set(k.toString, v.toString))

    sparkAppConf

  }

}
