package ad.uda.moro;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Servei;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

/**
 * Class <code><b>EnquestesServiceMBean</b></code> is a JSF Managed Bean.
 * @author sesiom
 */
@ManagedBean(name = "enquestesServiceMBean")
@SessionScoped
public class EnquestesServiceMBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to WebOperations instance
	private boolean initialized = false; // To indicate whether the managed bean is initialized
	private String errorMessage = " "; // Messages
	private boolean modeForm; // To indicate in which mode we are working (read, update...)
	

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Krypton entities:
	private List<Servei> serveiList;
	private Servei servei;
	
	/**
	 * Default constructor, Performs the following operations:
	 * <li>Takes the <code>WebOperations</code> instance</li>
	 * <li>Reset context</li>
	 * <li>Obtain reference to <code>EnquestesServiceRemote</code> Session interface</li>
	 * </ul>
	 */
	public EnquestesServiceMBean() {
		
		WebOperations.log("EnquestesServiceMBean: Default constructor called");
		
		// Obtain the web operations instance which gives access to common context and data:
		this.operations = WebOperations.getInstance();
		if (!operations.isInitialized()) {
			this.initialized = false;
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application not initialized", null));
			this.errorMessage = "Application not initialized";
			return;
		}

		// Get all references to EJB session beans:
		this.enquestesService = operations.getEnquestesServiceRemote();
		
		// Load the Establishment list:
		this.loadServeis();

		// Update context:
		this.initialized = true;
	}
	
	/*
	 * Getters and setters:
	 */
	
	public List<Servei> getServeiList() {
		return serveiList;
	}
	
	public Servei getServei() {
		return servei;
	}
	public void setServeiList(List<Servei> serveiList) {
		this.serveiList = serveiList;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isModeForm() {
		return modeForm;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	
	/*
	 * Other methods:
	 */


	/**
	 * Called when the user selects an Establishment
	 * @return the outcome
	 */
	public String establishmentSelected() {
		this.modeForm = true;
		return WebOperations.OUTCOME_SUCCESS;
	}
	
	/**
	 * Loads the Establishment collection in a field in this class.
	 * @return the outcome
	 */
	private String loadServeis() {
		
		WebOperations.log("EnquestesServiceMBean: loadServeis() called.");

		try {
			Servei[] serveiArray = this.enquestesService.getServeiList();
			this.serveiList = Arrays.asList(serveiArray);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}
}