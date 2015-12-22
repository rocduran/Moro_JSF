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
import ad.uda.moro.ejb.entity.ActivitatDossier;
import ad.uda.moro.ejb.entity.Dossier;
import ad.uda.moro.ejb.entity.Servei;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

@ManagedBean(name = "activitatDossierMBean")
@SessionScoped
public class ActivitatDossierMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to
	private String errorMessage = " "; // Messages

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Moro entities:
	private List<ActivitatDossier> activitatDossierList;
	private ActivitatDossier activitatDossier = new ActivitatDossier();

	public ActivitatDossierMBean() {
		WebOperations.log("ActivitatDossierMBean: Default constructor called");

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
		this.loadActivitatDossiers();
	}

	private String loadActivitatDossiers() {

		WebOperations.log("EnquestesServiceMBean: loadServeis() called.");

		try {
			this.activitatDossierList = new ArrayList<ActivitatDossier>();
			ActivitatDossier[] activtatDossierArray = this.enquestesService.getActivitatDossierList();
			this.activitatDossierList = Arrays.asList(activtatDossierArray);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}

	public List<ActivitatDossier> getActivitatDossierList() {
		return activitatDossierList;
	}

	public void setActivitatDossierList(List<ActivitatDossier> activitatDossierList) {
		this.activitatDossierList = activitatDossierList;
	}
	
	public ActivitatDossier getActivitatDossier() {
		return activitatDossier;
	}

	public void setActivitatDossier(ActivitatDossier activitatActivitatDossier) {
		this.activitatDossier = activitatActivitatDossier;
	}

	public void addActivitatDossier() {
		WebOperations.log("ActivitatDossierMBean: addActivitatDossier " + activitatDossier);

		try {
			this.enquestesService.addActivitatDossier(activitatDossier);
			this.loadActivitatDossiers();
			this.activitatDossier = new ActivitatDossier();
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}

	public void delete(int id) {
		WebOperations.log("ActivitatDossierMBean: delete ActivitatDossier " + id);

		try {
			this.enquestesService.deleteActivitatDossier(id);
			this.loadActivitatDossiers();
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}

	public void updateIdDossier(int id, int idDossier) {
		System.out.println("ActivitatDossierMBean updateIdDossier() id: "+id+" idDossier: "+ idDossier);
		try {
			ActivitatDossier a = this.enquestesService.getActivitatDossierById(id);
			Dossier d = this.enquestesService.getDossierById(idDossier);
			a.setIdDossier(d);
			this.enquestesService.updateActivitatDossier(a);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}

	public void updateIdServei(int id, int idServei) {
		System.out.println("ActivitatDossierMBean updateIdServei() id: "+id+" idDossier: "+ idServei);
		try {
			ActivitatDossier a = this.enquestesService.getActivitatDossierById(id);
			Servei s = this.enquestesService.getServeiById(idServei);
			a.setIdServei(s);
			this.enquestesService.updateActivitatDossier(a);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage);
		}
	}
}