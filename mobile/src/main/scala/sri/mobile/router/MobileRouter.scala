package sri.mobile.router

import chandu0101.macros.tojs.JSMacroAny
import sri.core.ElementFactory._
import sri.core._
import sri.mobile.U
import sri.mobile.components._
import sri.mobile.styles.MobileStyleSheet
import sri.mobile.styles.MobileStyleSheet._
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


sealed trait Page

trait StaticPage extends Page

trait DynamicPage[T] extends Page

sealed trait Route {
  def toJson: js.Dynamic
}


case class NavigatorRoute(title: String, component: js.Any, props: js.UndefOr[Any] = js.undefined, page: Page, sceneConfig: js.UndefOr[NavigatorSceneConfig] = js.undefined) {
  val toJS = JSMacroAny[NavigatorRoute](this)
}

object NavigatorRoute {
  def fromJson(obj: js.Dynamic) = {
    NavigatorRoute(title = obj.title.toString, component = obj.component, props = obj.props, page = obj.page.asInstanceOf[Page], sceneConfig = obj.sceneConfig.asInstanceOf[NavigatorSceneConfig])
  }

}

object MobileRouter {

  val sceneStyle = MobileStyleSheet.style(marginTop := 44)


  object MobileRouterContext {


    @ScalaJSDefined
    class Component extends ReactComponent[Props, Unit] {
      def render() = children

      def getChildContext() = {
        js.Dictionary("routerctrl" -> props.ctrl)
      }
    }

    case class Props(ctrl: MobileRouterCtrl)

    val factory = getComponentFactory(js.constructorOf[Component], classOf[Component])

    js.constructorOf[Component].childContextTypes = routerContextTypes

    def apply(ctrl: MobileRouterCtrl, key: U[String] = js.undefined, ref: js.Function1[Component,_] = null)(children: ReactNode*) = createElementWithChildren(factory, Props(ctrl), key = key, ref = ref)(children: _*)

  }


  @ScalaJSDefined
  class Component extends ReactComponent[Props, Unit] {

    override def render(): ReactElement = {

      Navigator(renderScene = renderScene _,
        //        sceneStyle = props.sceneStyle,
        initialRoute = props.config.initialRoute._2.toJS,
        configureScene = configureScene _)()
    }

    var ctrl: MobileRouterCtrl = _

    def configureScene(route: js.Dynamic) = {
      if (!js.isUndefined(route.sceneConfig)) route.sceneConfig.asInstanceOf[NavigatorSceneConfig]
      else NavigatorS.SceneConfigs.FloatFromRight
    }

    def renderScene(route: js.Dynamic, nav: NavigatorM) = {
      if (ctrl == null) ctrl = new MobileRouterCtrl(nav, props.config)
      MobileRouterContext(ctrl)(props.config.renderScene(NavigatorRoute.fromJson(route))
      )
    }

  }

  case class Props(config: MobileRouterConfig, sceneStyle: js.Any)

  val factory = getComponentFactory(js.constructorOf[Component], classOf[Component])

  def apply(routerConfig: MobileRouterConfig, sceneStyle: js.Any = sceneStyle) = createElement(factory, Props(routerConfig, sceneStyle))

}
