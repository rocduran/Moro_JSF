package ad.uda.moro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import ad.uda.moro.MoroException;
import ad.uda.moro.ejb.entity.Parametre;
import ad.uda.moro.ejb.entity.Valoracio;
import ad.uda.moro.ejb.session.EnquestesServiceRemote;

@ManagedBean(name = "valoracioMBean")
@SessionScoped
public class ValoracioMBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebOperations operations = null; // Contains reference to
	private String errorMessage = " "; // Messages

	// aoj_Moro session interfaces:
	private EnquestesServiceRemote enquestesService;

	// Moro entities:
	private List<Valoracio> valoracioList;
	private Valoracio selectedValoracio;

	private List<Parametre> parametresGraf;
	private List<Float> mitjanes;

	private BarChartModel barModel;

	public ValoracioMBean() {
		WebOperations.log("ValoracioMBean: Default constructor called");

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

		// Load the Valoracio list:
		this.loadValoracions();

		this.parametresGraf = new ArrayList<Parametre>();
		this.mitjanes = new ArrayList<Float>();

		this.createBarModels();
		
	}

	public String loadValoracions() {

		WebOperations.log("EnquestesServiceMBean: loadValoracions() called.");

		try {
			Valoracio[] valoracions = this.enquestesService.getValoracioList();
			this.valoracioList = Arrays.asList(valoracions);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return WebOperations.OUTCOME_ERROR;
		}
		return WebOperations.OUTCOME_SUCCESS;
	}

	public String loadValoracionsByIdServei(int idServei) {

		WebOperations.log("EnquestesServiceMBean: loadValoracions() called.");

		try {
			Valoracio[] valoracions = this.enquestesService.getValoracioServei(idServei);
			this.valoracioList = Arrays.asList(valoracions);
		} catch (MoroException ex) {
			errorMessage = WebOperations.getStandardBundleMessage("ErrBusinessLogic") + " " + ex.getMessage();
			WebOperations.addErrorMessage(errorMessage); // Add the error to the
															// Faces context
			return null;
		}
		return null;
	}
	
	public void loadParametresOfServei(int idServei) {
		for (int i = 0; i < valoracioList.size(); i++){
			if(!parametresGraf.contains(valoracioList.get(i).getIdParam()) &&
					valoracioList.get(i).getIdServei().getId() == idServei){
				parametresGraf.add(valoracioList.get(i).getIdParam());
			}
		}
	}

	public Valoracio getSelectedValoracio() {
		return selectedValoracio;
	}

	public void setSelectedValoracio(Valoracio selectedValoracio) {
		this.selectedValoracio = selectedValoracio;

	}

	public List<Valoracio> getValoracioList() {
		return valoracioList;
	}

	public List<Valoracio> getValoracioList(String idServei) {
		return valoracioList;
	}

	public void setValoracioList(List<Valoracio> valoracioList) {
		this.valoracioList = valoracioList;
	}

	public float getMitjanaParametre(int idParam, int idServei) {

		float suma = 0;
		float total = 0;

		for (int i = 0; i < valoracioList.size(); i++) {
			if (valoracioList.get(i).getIdParam().getId() == idParam 
			 && valoracioList.get(i).getIdServei().getId() == idServei) {
				suma += valoracioList.get(i).getValor();
				total++;
			}
		}
		
		if (total == 0)return (float) 0;
		return (float) suma / total;
	}

	public List<Parametre> getParametresGraf() {
		return this.parametresGraf;
	}

	public void setParametresGraf(int idServei) {
		
		this.parametresGraf = new ArrayList<Parametre>();
		this.mitjanes = new ArrayList<Float>();

		this.loadParametresOfServei(idServei);
		
		this.setMitjanesParametres(idServei);
		
		this.createBarModels();
		
	}

	public List<Float> getMitjanesParametres() {
		return this.mitjanes;
	}

	public void setMitjanesParametres(int idServei) {
		this.mitjanes = new ArrayList<Float>();

		for (int i = 0; i < valoracioList.size(); i++) {
			if (valoracioList.get(i).getIdServei().getId() == idServei) {
				this.mitjanes.add(getMitjanaParametre(valoracioList.get(i).getIdParam().getId(), idServei));
			}

		}
	}

	public void createBarModels() {
		createBarModel();
	}

	private void createBarModel() {
		barModel = initBarModel();

		barModel.setTitle("Grafica");
		barModel.setLegendPosition("ne");

		Axis xAxis = barModel.getAxis(AxisType.X);
		xAxis.setLabel("Parametres");

		Axis yAxis = barModel.getAxis(AxisType.Y);
		yAxis.setLabel("Valor");
		yAxis.setMin(0);
		yAxis.setMax(6);
	}

	private BarChartModel initBarModel() {
		BarChartModel model = new BarChartModel();

		ChartSeries params = new ChartSeries();
		params.setLabel("Parametres");

		if (this.parametresGraf.size() == 0) {
			params.set("", 0);
		}

		for (int i = 0; i < this.parametresGraf.size(); i++) {

			params.set(parametresGraf.get(i).getDescripcio(), mitjanes.get(i));

		}
		model.addSeries(params);
		return model;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

}