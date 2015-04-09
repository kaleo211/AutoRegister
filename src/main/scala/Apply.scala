package apply

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.NameValuePair
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.protocol.HTTP
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import org.apache.http.cookie.Cookie

import org.htmlcleaner.HtmlCleaner
import org.htmlcleaner.TagNode
import org.htmlcleaner.XPatherException

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.io._
import java.net.{URL, URI}
import java.util.ArrayList

import scala.io.Source


/**
 *
 * @author hexuebin0201@gmail.com (he xuebin)
 **/
object Apply {

  val logger = LoggerFactory.getLogger(this.getClass)
  //org.apache.log4j.BasicConfigurator.configure
  val httpClient = new DefaultHttpClient

  val equipID = "12"
  val iIIDDic = "50040:1"
  val dir = "/home/kaleo211/Tmp/"

  val url = "http://yqgx.shu.edu.cn/ZOrder/OrderByIntern.aspx"
  val uri = new URIBuilder(new URI(url)).setQuery("EquipID=" + equipID +
                                                  "&IIIDDic= " + iIIDDic + "|&IsDelegate=True").build


  // txtMytFundCardPersonRsp
  // txtMyfundCardPjtName", ""))
  // txtFundCardNo", ""))
  // txtFundCardPersonRsp", ""))
  // txtFundCardPjtName", ""))
  // txtTestName", "11"))
  // txtTestCode", "11"))
  // drpProjectType", "3"))
  // drpIndustryField", "5"))
  // txtTestContent", "11"))
  // hid551Price", "10.00"))
  // hid551Desc", ""))
  // hid551UnitName", "个"))
  // txt551Amount", "1"))

  def main(args: Array[String]) = {

    login

    getTimeList

    // getApplyForm

    // trySubmitApplyForm

    // submitApplyForm
  }


  def login = {
    val cleaner = new HtmlCleaner
    val html = cleaner.clean(new URL("http://yqgx.shu.edu.cn/Index.aspx"), "utf-8")
    val viewState = html.findElementByAttValue("name", "__VIEWSTATE", true, true)
                        .getAttributeByName("value")
    val eventValidation = html.findElementByAttValue("name", "__EVENTVALIDATION", true, true)
                              .getAttributeByName("value")

    var httpPost = new HttpPost("http://yqgx.shu.edu.cn/Index.aspx")
    val nvps = new ArrayList[NameValuePair]
    nvps.add(new BasicNameValuePair("ctl00$txtUseName", "11722157"))
    nvps.add(new BasicNameValuePair("ctl00$txtPassWord", "wxhxb1314"))
    nvps.add(new BasicNameValuePair("ctl00$btnLogin", "登录"))
    nvps.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$ddlContry", "0"))
    nvps.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$ddlUnit", "0"))
    nvps.add(new BasicNameValuePair("__VIEWSTATE", viewState))
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation))
    httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8))

    try {
      val response = httpClient.execute(httpPost)
      val statusCode = response.getStatusLine.getStatusCode
      println("statusCode: " + statusCode)
    } catch {
      case e: Exception =>
        httpPost.abort
        println("error" + e)
    } finally {
      httpPost.releaseConnection
    }
  }

  def getApplyForm = {

    var fileOutputStream: FileOutputStream = null
    var httpGet = new HttpGet
    try {
      httpGet.setURI(uri)

      val response = httpClient.execute(httpGet)
      val statusCode = response.getStatusLine.getStatusCode
      println("statusCode: " + statusCode)
      if (statusCode == 200) {
        val entity = response.getEntity
        if (entity != null) {
          fileOutputStream = new FileOutputStream(new File(dir + "form.html"))
          entity.writeTo(fileOutputStream)
        }
      }
    } catch {
      case e: Exception =>
        httpGet.abort
        println("error" + e)
    } finally {
      httpGet.releaseConnection
    }
  }

  def trySubmitApplyForm = {
    val cleaner = new HtmlCleaner
    val html = cleaner.clean(new File(dir + "form.html"), "utf-8")
    val viewState = html.findElementByAttValue("name", "__VIEWSTATE", true, true)
                        .getAttributeByName("value")
    val eventValidation = html.findElementByAttValue("name", "__EVENTVALIDATION", true, true)
                              .getAttributeByName("value")

    val nvps = new ArrayList[NameValuePair]
    nvps.add(new BasicNameValuePair("SM", "UPOrderRecord|drpOfferMethod"))
    nvps.add(new BasicNameValuePair("drpOfferMethod", "0"))
    nvps.add(new BasicNameValuePair("drpOperateWay", "1"))
    nvps.add(new BasicNameValuePair("drpMyFundCardList", ">>请选择<<"))
    nvps.add(new BasicNameValuePair("txtMytFundCardPersonRsp", ""))
    nvps.add(new BasicNameValuePair("txtMyfundCardPjtName", ""))
    nvps.add(new BasicNameValuePair("txtFundCardNo", ""))
    nvps.add(new BasicNameValuePair("txtFundCardPersonRsp", ""))
    nvps.add(new BasicNameValuePair("txtFundCardPjtName", ""))
    nvps.add(new BasicNameValuePair("txtTestName", ""))
    nvps.add(new BasicNameValuePair("txtTestCode", ""))
    nvps.add(new BasicNameValuePair("drpProjectType", "-1"))
    nvps.add(new BasicNameValuePair("drpIndustryField", "-1"))
    nvps.add(new BasicNameValuePair("txtTestContent", ""))
    nvps.add(new BasicNameValuePair("hid551Price", "10.00"))
    nvps.add(new BasicNameValuePair("hid551Desc", ""))
    nvps.add(new BasicNameValuePair("hid551UnitName", "个"))
    nvps.add(new BasicNameValuePair("txt551Amount", "0"))
    nvps.add(new BasicNameValuePair("txt551PriceType", "0"))
    nvps.add(new BasicNameValuePair("btnSave", "提 交"))
    nvps.add(new BasicNameValuePair("__EVENTARGUMENT", ""))
    nvps.add(new BasicNameValuePair("__LASTFOCUS", ""))
    nvps.add(new BasicNameValuePair("__ASYNCPOST", "true"))
    nvps.add(new BasicNameValuePair("__VIEWSTATE", viewState))
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation))
    nvps.add(new BasicNameValuePair("__EVENTTARGET", "drpOfferMethod"))

    var httpPost = new HttpPost
    httpPost.setURI(uri)
    httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8))
    httpPost.addHeader("X-MicrosoftAjax", "Delta=true")
    httpPost.addHeader("X-Requested-With", "XMLHttpRequest")
    httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22")
    httpPost.addHeader("Referer", "http://yqgx.shu.edu.cn/ZOrder/OrderByIntern.aspx?EquipID=282&IIIDDic=50040:2|&IsDelegate=True")
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    httpPost.addHeader("Accept", "*/*")
    httpPost.addHeader("Host", "yqgx.shu.edu.cn")
    httpPost.addHeader("Connection", "keep-alive")
    httpPost.addHeader("Origin", "http://yqgx.shu.edu.cn")

    var fileOutputStream: FileOutputStream = null
    try {
      val response = httpClient.execute(httpPost)
      val statusCode = response.getStatusLine.getStatusCode
      println("statusCode: " + statusCode)
      if (statusCode == 200) {
        val entity = response.getEntity
        if (entity != null) {
          fileOutputStream = new FileOutputStream(new File(dir + "result0.html"))
          entity.writeTo(fileOutputStream)
        }
      }
    } catch {
      case e: Exception =>
        httpPost.abort
        println("error" + e)
    } finally {
      httpPost.releaseConnection
    }
  }

  def submitApplyForm = {

    val html = Source.fromFile(new File(dir + "result0.html")).mkString.filter(c => c != '\n' && c != '\r')

    val vp = ".*__VIEWSTATE\\|([^|]*)\\|.*".r
    val vp(viewState) = html

    val ep = ".*__EVENTVALIDATION\\|([^|]*)\\|.*".r
    val ep(eventValidation) = html

    val nvps = new ArrayList[NameValuePair]
    nvps.add(new BasicNameValuePair("SM", "uptips|btnSave"))
    nvps.add(new BasicNameValuePair("drpOfferMethod", "0"))
    nvps.add(new BasicNameValuePair("drpOperateWay", "1"))
    nvps.add(new BasicNameValuePair("drpMyFundCardList", ">>请选择<<"))
    nvps.add(new BasicNameValuePair("txtMytFundCardPersonRsp", ""))
    nvps.add(new BasicNameValuePair("txtMyfundCardPjtName", ""))
    nvps.add(new BasicNameValuePair("txtFundCardNo", ""))
    nvps.add(new BasicNameValuePair("txtFundCardPersonRsp", ""))
    nvps.add(new BasicNameValuePair("txtFundCardPjtName", ""))
    nvps.add(new BasicNameValuePair("txtTestName", "py"))
    nvps.add(new BasicNameValuePair("txtTestCode", "E.25-0409-00-003"))
    nvps.add(new BasicNameValuePair("drpProjectType", "3"))
    nvps.add(new BasicNameValuePair("drpIndustryField", "5"))
    nvps.add(new BasicNameValuePair("txtTestContent", "11"))
    nvps.add(new BasicNameValuePair("hid551Price", "10.00"))
    nvps.add(new BasicNameValuePair("hid551Desc", ""))
    nvps.add(new BasicNameValuePair("hid551UnitName", "个"))
    nvps.add(new BasicNameValuePair("txt551Amount", "1"))
    nvps.add(new BasicNameValuePair("txt551PriceType", "0"))
    nvps.add(new BasicNameValuePair("btnSave", "提 交"))
    nvps.add(new BasicNameValuePair("__EVENTARGUMENT", ""))
    nvps.add(new BasicNameValuePair("__LASTFOCUS", ""))
    nvps.add(new BasicNameValuePair("__ASYNCPOST", "true"))
    nvps.add(new BasicNameValuePair("__VIEWSTATE", viewState))
    nvps.add(new BasicNameValuePair("__EVENTVALIDATION", eventValidation))
    nvps.add(new BasicNameValuePair("__EVENTTARGET", ""))

    var httpPost = new HttpPost
    httpPost.setURI(uri)
    httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8))
    httpPost.addHeader("X-MicrosoftAjax", "Delta=true")
    httpPost.addHeader("X-Requested-With", "XMLHttpRequest")
    httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22")
    httpPost.addHeader("Referer", "http://yqgx.shu.edu.cn/ZOrder/OrderByIntern.aspx?EquipID=282&IIIDDic=50040:2|&IsDelegate=True")
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
    httpPost.addHeader("Accept", "*/*")
    httpPost.addHeader("Connection", "keep-alive")

    var fileOutputStream: FileOutputStream = null
    try {
      val response = httpClient.execute(httpPost)
      val statusCode = response.getStatusLine.getStatusCode
      println("statusCode: " + statusCode)
      if (statusCode == 200) {
        val entity = response.getEntity
        if (entity != null) {
          fileOutputStream = new FileOutputStream(new File(dir + "result1.html"))
          entity.writeTo(fileOutputStream)
        }
      }
    } catch {
      case e: Exception =>
        httpPost.abort
        println("error" + e)
    } finally {
      httpPost.releaseConnection
    }
  }

  def getTimeList = {
    var fileOutputStream: FileOutputStream = null
    var httpGet = new HttpGet
    var cleaner = new HtmlCleaner
    val xPath = "//table[@class='GridSelf']//td"
    val bw = new BufferedWriter(new FileWriter(new File(dir + "timelist.txt")))

    try {
      for (i <- 50000 to 99999) {
        val test = i.toString + ":1"
        println("i: " + i)

        val testUri = new URIBuilder(new URI(url)).setQuery("EquipID=" + equipID +
                                                  "&IIIDDic= " + test + "|&IsDelegate=True").build
        httpGet.setURI(testUri)

        try {
          val response = httpClient.execute(httpGet)
          val statusCode = response.getStatusLine.getStatusCode
          println("statusCode: " + statusCode)
          if (statusCode == 200) {
            val entity = response.getEntity
            if (entity != null) {
              val file = new File(dir + "date/" + i.toString + ".html")
              fileOutputStream = new FileOutputStream(file)
              entity.writeTo(fileOutputStream)
              val array = cleaner.clean(file).evaluateXPath(xPath)
              if (!array.isEmpty) {
                val start_time = array(0).asInstanceOf[TagNode].getText.toString
                val end_time = array(1).asInstanceOf[TagNode].getText.toString
                println("i: " + i.toString + " from: " + start_time + " to " + end_time)
                bw.write(start_time + "  -  " + end_time + "  / " + i.toString)
                bw.newLine
                bw.flush
              }
              //file.delete
            }
          }
        } catch {
          case e: Exception =>
          httpGet.abort
        } finally {
          httpGet.releaseConnection
        }
      }
    } catch {
      case e: Exception =>
      println("error " + e.printStackTrace)
    } finally {
      bw.close
    }
  }
}
