package ad.uda.moro;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ad.uda.moro.ejb.session.EnquestesServiceRemote;

/**
 * The <code><b>WebOperations</b></code> class contains utility methods for the KryptonWeb application.<br/>
 * The following items are implemented:
 * <ul>
 * <li>Symbols for action outcomes, used in JSF navigation rules</li>
 * <li>Message bundle access</li>
 * <li>Enterprise Java Bean (EJB) JNDI initialization and lookup</li>
 * <li>Location of EJB Session interfaces for beans, used in this application</li>
 * <li>Faces context information manipulation</li>
 * </ul>
 * The class implements the <code>javax.servlet.ServletContextListener</code> interface and will be initialized when notified that the
 * application context is initialized via the <code>contextInitialized()</code> method.
 * 
 * @author Alexander Beening Jansen - PA3CLV / C33AB
 * @see WebOperations#contextInitialized(ServletContextEvent)
 */
public class WebOperations implements ServletContextListener, Serializable {

	// Class context:
	private static final long serialVersionUID = 1L;
	private static WebOperations INSTANCE = null; // This will hold the instance of this class

	// Outcome texts, used in navigation-cases in faces-config.xml:
	protected static final String OUTCOME_ERROR = "error";
	protected static final String OUTCOME_SUCCESS = "success";
	protected static final String FORMATSTRING_ID = "%d";

	// Global symbols:
	/** Date/time format, used in this web application */
	public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	/** Date/time format, used in this web application for minute precision time stamps */
	public static final String DATETIME_MINUTE_FORMAT = "dd/MM/yyyy HH:mm";
	/** Date/time format, used in this web application for day precision time stamps */
	public static final String DATETIME_DAY_FORMAT = "dd/MM/yyyy";
	protected static final String STANDARD_MESSAGE_BUNDLE = "bundles.messages";

	// Remoting and EJB references:
    private Context initialContext;
    private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
	private EnquestesServiceRemote enquestesService;

	// Other context:
	private boolean initialized = false; // Flag to indicate that the WebOperations instance been initialized
	private static final String LOGPREFIX = "WebOperations -- "; // Logger prefix text
	private static ExternalContext externalContext = null; // Faces Context instance. Will be initialized in 
	
	/**
	 * Default constructor.
	 */
	private WebOperations() {
		
	}

	/**
	 * Returns the instance of this class, created by the web application environment
	 * @return The instance
	 */
	public static WebOperations getInstance() {
		return INSTANCE;
	}

	/**
	 * Indicates whether the instance is initialized and can be used by managed bean instances
	 * @return <b>true</b> if initialized, <b>false</b> if not.
	 */
	public boolean isInitialized() {
		return initialized;
	}
		
	
	/**
	 * Returns the reference to the MoroEJB EnquestesServiceRemote interface
	 * @return The reference as an instance of interface <code>EnquestesServiceRemote</code>
	 */
	protected EnquestesServiceRemote getEnquestesServiceRemote() {
		return this.enquestesService;
	}
	
	/**
	 * Returns the contents of a request parameter, passed via a tag (<code>f:param core tag</code> to a method (action) in a managed bean.
	 * @param parameterName The name of the parameter.
	 * @return A String, containing the contents. returns an empty string ("") if the parameter does not exist.
	 */
	public static String getRequestTextParameter(String parameterName) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ext = ctx.getExternalContext();
		Map<?, ?> map = ext.getRequestParameterMap();
		Object param = map.get(parameterName);
		if ((param != null) && (param instanceof String)) {
			return (String) param;
		}
		return "";
	}

	/**
	 * Returns the contents of a bundle message, using the bundle and message name
	 * @param bundleName The name of the bundle. Must be full classified (ex: <code>bundles.messages</code>.
	 * @param messageName The message label as specified in the properties file
	 * @return A String, containing the contents of the message.
	 */
	public static String getBundleMessage(String bundleName, String messageName) {
		ResourceBundle rb = ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot().getLocale());
		String content = rb.getString(messageName);
		return content;
	}

	/**
	 * Returns the contents of a bundle message, using bundle <code>bundles.Messages</code>
	 * 
	 * @param messageName
	 *            The message label as specified in the properties file. Must be present in the standard message bundle
	 *            <code>bundles.Messages</code>.
	 * @return A String, containing the contents of the message.
	 */
	public static String getStandardBundleMessage(String messageName) {
		String txt = getBundleMessage(STANDARD_MESSAGE_BUNDLE, messageName);
		if (txt == null) {
			return "MISSING. Label = [" + messageName + "]";
		}
		return txt;
	}
	
	/**
	 * Adds a new information message to the message queue. The message can be visualized using the specific components in the XHTML pages
	 * @param summary The summary message text
	 */
	protected static void addInfoMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", summary);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Adds a new warning message to the message queue. The message can be visualized using the specific components in the XHTML pages
	 * @param summary The summary message text
	 */
	protected static void addWarningMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", summary);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Adds a new error message to the message queue. The message can be visualized using the specific components in the XHTML pages
	 * @param summary The summary message text
	 */
	protected static void addErrorMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", summary);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	/**
	 * Method to log messages, using the FacesContext instance. Logging will be visible in the WIldFly console and corresponding log files
	 * @param message the message to log.
	 */
	public static void log(String message) {

		if (message == null) return; // Omit null messages
		
		// Handle ExternalContext not available:
		if (WebOperations.externalContext == null) {
			System.out.println("Logger not available -- " + message);
			return;
		}
		
		// Log the message:
		WebOperations.externalContext.log(message);
	}

	
	/**
	 * <code>ServletContextListener</code> interface implementation.<br/>
	 * This method is called by WildFly's Servlet engine when this RESTful web service is destroyed and no longer available to consumers.
	 * No real actions will be performed at this moment. Just a log entry about the event will be visible.
	 * @param servletContext The servlet context instance
	 */
	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		WebOperations.log(LOGPREFIX + "Servet context DESTROYED");
	}

	/**
	 * <code>ServletContextListener</code> interface implementation.<br/>
	 * This method is called by WildFly's Servlet engine when this RESTful web service is initialized and ready to process requests.<p/>
	 * Typically, the following actions will be performed:<ul>
	 * <li>Faces External context initialization</li>
	 * <li>Remoting InitialContext preparation</li>
	 * <li>Remote Lookup of all KryptonEJB Session Interfaces to grant access to the business logic</li>
	 * <li>Storing the reference to the single instance of this class to make it available for Resource Classes</li>
	 * <ul>
	 * @param servletContextEvent The servlet context event instance
	 * 
	 */
	@Override
	public void contextInitialized(ServletContextEvent evt) {
		
		// Initialize ExternalContext instance:
		WebOperations.externalContext = FacesContext.getCurrentInstance().getExternalContext();
		
		// Prepare the initial context for remote EJB access at WildFly server:
        Properties properties = new Properties();
        properties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
        try {
        	initialContext = new InitialContext(properties);
		} catch (NamingException ex) {
			WebOperations.log("Failed to obtain the EJB references. Details: " + ex.getMessage());
			return;
		}
        WebOperations.log(LOGPREFIX + "Initial context created");
		
		// Get the reference to the EJBs:
		try {
			
			WebOperations.log(LOGPREFIX + "Look up EnquestesServiceRemote EJB...");
			enquestesService = (EnquestesServiceRemote)initialContext.lookup(CommonUtilities.getLookupEJBName("EnquestesServiceBean", EnquestesServiceRemote.class.getName()));
			WebOperations.log(LOGPREFIX + "EnquestesServiceRemote EJB LOCATED");
			
		} catch (NamingException ex) {
			WebOperations.log(LOGPREFIX + "ERROR: " + ex.getMessage());
			return;
		}
		
		// Store the reference to this instance to make it available via method getInstance():
		this.initialized = true; // Mark instance initialized
		INSTANCE = this; // Set the instance to this one.
		WebOperations.log(LOGPREFIX + "RESTful web service INITIALIZED");

	}

}
