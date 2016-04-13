package actions;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import controllers.routes;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class BasicAuthAction extends Action.Simple {
	
	private static final String AUTHORIZATION = "authorization";
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    
    private final String realm;
    private String username;
    private char[] password;
    
    public BasicAuthAction() {
    	Config config = ConfigFactory.load("wedding.conf").getConfig("wedding.auth");
    	username = config.getString("username");
    	password = config.getString("password").toCharArray();
    	realm = "Basic realm=\"" + config.getString("realm") + "\"";
    }
    
	@Override
	public Promise<Result> call(Context context) throws Throwable {
		if (!context.request().secure()) {
			// You're in the wrong place
			return Promise.pure(redirect(routes.Application.defaultRoute("error")));
		}
		
		String authHeader = context.request().getHeader(AUTHORIZATION);
        if (authHeader == null) {
            return retryResponse(context);
        }

        String auth = authHeader.substring(6);
        byte[] decodedAuth = new sun.misc.BASE64Decoder().decodeBuffer(auth);
        String[] credString = new String(decodedAuth, "UTF-8").split(":");

        if (credString == null || credString.length != 2 ||
        		!credString[0].equals(username) || !credString[1].equals(new String(this.password))) {
        	// Incorrect or missing password/username
            return retryResponse(context);
        }

        return delegate.call(context);
	}
	
	private Promise<Result> retryResponse(Context context) {
		context.response().setHeader(WWW_AUTHENTICATE, realm);
        return Promise.pure(unauthorized());
	}

}
