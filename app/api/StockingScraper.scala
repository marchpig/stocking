package api

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import com.github.nscala_time.time.Imports._

import scala.concurrent.Future
import play.api.libs.json._

object StockingScraper {
  val DateTimeLimit = new DateTime() - 1.days

  val companyList = List(Company("CJ CGV", "079160"), Company("이-글 벳", "044960"));

  def get(): Future[JsValue] = {
    Future.successful {
      val mergedItemList = companyList.foldLeft(List[Item]())((mergedItemList: List[Item], company: Company) => {
        mergedItemList ::: company.itemList
      }).sortBy(_.dateTime).reverse
      Json.toJson(mergedItemList)
    }
  }

  implicit val implicitWrites = new Writes[Item] {
    def writes(item: Item): JsValue = {
      Json.obj(
        "companyName" -> item.companyName,
        "dateTime" -> item.dateTime,
        "title" -> item.title,
        "userId" -> item.userId,
        "url" -> item.url
      )
    }
  }
}

case class Company(name: String, code: String) {
  val browser = JsoupBrowser()
  val url = s"http://finance.naver.com/item/board.nhn?code=$code&page="
  lazy val itemList = getItemList(url, 1, List[Item]()).filter(_.dateTime > StockingScraper.DateTimeLimit)

  @annotation.tailrec
  private def getItemList(boardUrl: String, pageNumber: Int, itemList: List[Item]): List[Item] = {
    val doc = browser.get(boardUrl + pageNumber)
    val itemElementList: List[Element] = (doc >> elementList("tr")).filter(_.hasAttr("align"))
    val itemListInOnePage = itemElementList.foldRight(List[Item]())((elem: Element, itemList: List[Item]) => {
      val dateTime = (elem >> text(".gray03")).toDateTime("yy.MM.dd HH:mm")
      val title = elem >> attr("title")("a")
      val userId = elem >> text("td.p11")
      val url = "http://finance.naver.com" + (elem >> attr("href")("a"))
      Item(name, dateTime, title, userId, url) :: itemList
    })

    if (itemListInOnePage.last.dateTime < StockingScraper.DateTimeLimit)
      itemList ::: itemListInOnePage
    else
      getItemList(boardUrl, pageNumber + 1, itemList ::: itemListInOnePage)
  }
}

case class Item(companyName: String, dateTime: DateTime, title: String, userId: String, url: String)
