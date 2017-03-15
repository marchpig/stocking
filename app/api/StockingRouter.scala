package api

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class StockingRouter @Inject()(controller: StockingController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/scrap") =>
      controller.scrap
  }

}
