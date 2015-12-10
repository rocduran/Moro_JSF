package ad.uda.moro;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Dossier;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

@ManagedBean(name = "dossierMBean")
@SessionScoped
public class DossierMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to
	private String errorMessage = " "; // Messages

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Moro entities:
	private List<Dossier> dossierList;
	private String selectedDossier;

	public DossierMBean() {
		WebOperations.log("DossierMBean: Default constructor called");

		// Obtain the web operations instance which gives access to common
		// context and data:
		this.operations = WebOperations.getInstance();
		if (!operations.isInitialized()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application not initialized", null));
			this.errorMessage = "Application not initialized";
			return;
		}

		// Get all references to EJB session beans:
		this.enquestesService = operations.getEnquestesServiceRemote();

		// Load the Servei list:
		this.loadDossiers();
	}

	private String loadDossiers() {

		WebOperations.log("EnquestesServiceMBean: loadServeis() called.");

		try {
			Dossier[] serveiArray = this.enquestesService.getDossierList();
			this.dossierList = Arrays.asList(serveiArray);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}

	public List<Dossier> getDossierList() {
		return dossierList;
	}

	public void setDossierList(List<Dossier> dossierList) {
		this.dossierList = dossierList;
	}

	public String getSelectedDossier() {
		return selectedDossier;
	}

	public void setSelectedDossier(String selectedDossier) {
		this.selectedDossier = selectedDossier;
	}

	
}