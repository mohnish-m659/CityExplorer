package com.citiesExplorer.main;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CityGUI {
	
	TableViewer viewer;
	
	String[] columns = {"City", "Country", "Population", "Longitude", "Latitude"};
	
	private static CityGUI instance = new CityGUI();
	
	private void CityGUI() {}

	public void create(Shell shell) {
		Composite parentComposite = new Composite(shell, SWT.None);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		parentComposite.setLayout(layout);
		
		initTable(parentComposite);
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
                	City city = CityTableModel.getDataAt(index);
                	item.setText(getData(city));
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Reached End of the data set update count");
					table.setItemCount(index);
				}
			}
		});
		table.setItemCount(100000);
		
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewerParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
