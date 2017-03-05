package marchpig.stocking

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import com.github.nscala_time.time.Imports._

object Scraper extends App {
  val DateTimeLimit = new DateTime() - 1.days

  val company = Company("CJ CGV", "079160")
  println(company.name)
  println(company.itemList.mkString("\n"))
  println(company.itemList.length)
}

case class Company(name: String, code: String) {
  val browser = JsoupBrowser()
  val url = s"http://finance.naver.com/item/board.nhn?code=$code&page="
  lazy val itemList = getItemList(url, 1)

  def getItemList(boardUrl: String, pageNumber: Int): List[Item] = {
    val doc = browser.get(boardUrl + pageNumber)
    val itemElementList: List[Element] = (doc >> elementList("tr")).filter(_.hasAttr("align"))
    val itemList = itemElementList.foldRight(List[Item]())((elem: Element, itemList: List[Item]) => {
      val dateTime = (elem >> text(".gray03")).toDateTime("yy.MM.dd HH:mm")
      val title = elem >> attr("title")("a")
      val userId = elem >> text("td.p11")
      val url = "http://finance.naver.com" + (elem >> attr("href")("a"))
      new Item(dateTime, title, userId, url) :: itemList
    })

    if (itemList.last.dateTime < Scraper.DateTimeLimit)
      itemList
    else
      itemList ::: getItemList(boardUrl, pageNumber + 1)
  }
}

case class Item(dateTime: DateTime, title: String, userId: String, url: String)
