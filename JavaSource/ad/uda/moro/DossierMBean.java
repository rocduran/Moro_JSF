package ad.uda.moro;

import java.io.Serializable;
import java.util.ArrayList;
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
	private Dossier dossier = new Dossier();

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
			this.dossierList = new ArrayList<Dossier>();
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
	
	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

	public void addDossier() {
		WebOperations.log("DossierMBean: addDossier " + dossier);

		try {
			this.enquestesService.addDossier(dossier);
			this.loadDossiers();
			this.dossier = new Dossier();
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}

	public void delete(int id) {
		WebOperations.log("DossierMBean: delete Dossier " + id);

		try {
			this.enquestesService.deleteDossier(id);
			this.loadDossiers();
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}

	public void updateDescripcio(int id, String descripcio) {
		try {
			Dossier dossier = this.enquestesService.getDossierById(id);
			dossier.setDescripcio(descripcio);
			this.enquestesService.updateDossier(dossier);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}

	public void updatePreu(int id, int preu) {
		try {
			Dossier dossier = this.enquestesService.getDossierById(id);
			dossier.setPreu(preu);
			this.enquestesService.updateDossier(dossier);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}
	
	public void test(){
		WebOperations.log("TEST TEST TEST TEST TEST TEST TEST TEST");
		WebOperations.log("Dossier.descripcio: " + dossier.getDescripcio());
		WebOperations.log("Dossier.preu: " + dossier.getPreu());
	}
}