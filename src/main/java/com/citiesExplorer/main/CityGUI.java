package com.citiesExplorer.main;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class CityGUI {
	
	Label cityLabel;
	Label countryLabel;
	Text cityText;
	Text countryText;
	
	Label sortByLabel;
	Combo sortByCombo;
	
	TableViewer viewer;
	private Map<String, String> criteria = new HashMap<>();
	private Map<String, String> sortOrder = new HashMap<>();
	
	private int widthHint = 600;
	
	String[] columns = {"City", "Country", "Population", "Longitude", "Latitude"};
	
	private static CityGUI instance = new CityGUI();
	
	private void CityGUI() {}

	public void create(Shell shell) {
		Composite parentComposite = new Composite(shell, SWT.None);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		parentComposite.setLayout(layout);
		
		initSearchSortForm(parentComposite);
		initTable(parentComposite);
		
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
	}
	
	private void initSearchSortForm(Composite parentComposite) {
		Composite parent = new Composite(parentComposite, SWT.None);
		parent.setLayout(new FillLayout());
		Group group = new Group(parent, SWT.SHADOW_NONE);
		group.setText("Search & Sort");
		group.setLayout(new GridLayout(2, false));
		
		GridData textLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		
		cityLabel = new Label(group, SWT.None);
		cityLabel.setText("City");
		
		cityText = new Text(group, SWT.SINGLE);
		cityText.setLayoutData(textLayoutData);
		
		countryLabel = new Label(group, SWT.None);
		countryLabel.setText("Country");
		
		countryText = new Text(group, SWT.SINGLE);
		countryText.setLayoutData(textLayoutData);
		
		sortByLabel = new Label(group, SWT.None);
		sortByLabel.setText("Sort By");
		
		sortByCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		sortByCombo.setItems("City", "Country", "Population");
		sortByCombo.select(0);
		sortByCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		GridData parentGd = new GridData(SWT.FILL, SWT.TOP, true, true);
		parentGd.heightHint = 130;
		parentGd.widthHint = widthHint;
		parent.setLayoutData(parentGd);
		
	}

	private void initTable(Composite parent) {
		Composite viewerParent = new Composite(parent, SWT.None);
		viewerParent.setLayout(new GridLayout(1, false));
		viewer = new TableViewer(viewerParent, SWT.V_SCROLL| SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = viewer.getTable();
		createColumns(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		table.addListener(SWT.SetData, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
                int index = table.indexOf(item);
                try {
                	City city = CityTableModel.getDataAt(index, criteria, sortOrder);
                	item.setText(getData(city));
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Reached End of the data set update count");
					table.setItemCount(index);
				}
			}
		});
		table.setItemCount(CityTableModel.getRowCount());
		
		GridData tableGd = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(tableGd);
		GridData parentGd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		parentGd.heightHint = 400;
		parentGd.widthHint = widthHint;
		viewerParent.setLayoutData(parentGd);
	}

	protected String[] getData(City city) {
		return new String[] {city.getName(), city.getCountry(), String.valueOf(city.getPopulation()),
				String.valueOf(city.getLongitude()), String.valueOf(city.getLatitude())};
	}

	private void createColumns(Table table) {
		for(int i=0; i<columns.length; i++) {
			TableColumn column = new TableColumn(table, SWT.None);
			column.setText(columns[i]);
			column.setWidth(120);
			column.setResizable(true);
			column.setMoveable(true);
		}
	}

	public static CityGUI getInstance() {
		return instance;
	}

}
