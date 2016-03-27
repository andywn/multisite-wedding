package controllers;

import play.Logger;
import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;

/**
 *
 * Default Controller, for views that aren't related Invites or Admin
 *
 */
public class Application extends Controller {
	
	public Result defaultRoute(String path) {
    	Logger.info("Attempted to access " + path);
    	return ok(views.html.error.error.render());
    }
    
    // Not logged.
    public Result test() {
    	return ok(views.html.error.error.render());
    }
    
    public Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
            Routes.javascriptRouter("jsRoutes",
                routes.javascript.Admin.toggleSent()
            )
        );
    }

}
