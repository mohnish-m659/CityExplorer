package com.citiesExplorer.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class Snippet144 {

	  static final int COUNT = 1000000;

	  public static void main(String[] args) {
	    Display display = new Display();
	    final Shell shell = new Shell(display);
	    shell.setLayout(new RowLayout(SWT.VERTICAL));
	    final Table table = new Table(shell, SWT.VIRTUAL | SWT.BORDER);
	    table.addListener(SWT.SetData, new Listener() {
	      public void handleEvent(Event event) {
	        TableItem item = (TableItem) event.item;
	        int index = table.indexOf(item);
	        item.setText("Item " + index);
	        System.out.println(item.getText());
	      }
	    });
	    table.setLayoutData(new RowData(200, 200));
	    Button button = new Button(shell, SWT.PUSH);
	    button.setText("Add Items");
	    final Label label = new Label(shell, SWT.NONE);
	    button.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	        long t1 = System.currentTimeMillis();
	        table.setItemCount(COUNT);
	        long t2 = System.currentTimeMillis();
	        label.setText("Items: " + COUNT + ", Time: " + (t2 - t1)
	            + " (ms)");
	        shell.layout();
	      }
	    });
	    shell.pack();
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	  }
	}