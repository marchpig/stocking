package api

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import play.api.mvc._

class StockingController @Inject()(implicit ec: ExecutionContext) extends Controller {

  def scrap: Action[AnyContent] = {
    Action.async { implicit request =>
      StockingScraper.get.map { contents =>
        Ok(contents)
      }
    }
  }

}
