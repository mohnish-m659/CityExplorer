package com.citiesExplorer.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citiesExplorer.utils.HibernateUtil;

public class CityMain {
	
	public static void main(String[] args) {
		Display display = new Display();
		Server server = null;
		try {
			//Access H2 console on localhost:8082
            //server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Shell splash = new Shell(display, SWT.TOOL | SWT.NO_TRIM);
        splash.setLayout(new FillLayout());
        new Label(splash, SWT.CENTER).setText("Initializing Database...");
        splash.setSize(300, 100);
        splash.setLocation(display.getPrimaryMonitor().getBounds().width / 2 - splash.getSize().x / 2,
                           display.getPrimaryMonitor().getBounds().height / 2 - splash.getSize().y / 2);
        splash.open();
		
		Future<Boolean> initialized = executor.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return initDatabase();
			}
		});
		
		try {
            if (initialized.get()) {
                splash.dispose();
                Shell shell = new Shell(display, SWT.SHELL_TRIM);
                shell.setLayout(new GridLayout(1, false));
                shell.setText("Cities Explorer");
                //shell.setSize(800, 500);

                CityGUI.getInstance().create(shell);

                shell.open();
                while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                display.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
            if(server != null) {
            	server.shutdown();
            }
            HibernateUtil.shutdown();
        }
	}

	private static boolean initDatabase() {
		 try (BufferedReader br = new BufferedReader(
					new InputStreamReader(CityMain.class.getClassLoader().getResourceAsStream("worldcities.csv")));
				 CSVParser parser = new CSVParser(br, CSVFormat.DEFAULT.withHeader());) {
			 
				int count = 0;
				
				for(CSVRecord record : parser) {
					if(count == 0) {
						count++;
						continue;
					}
					try {
						String name = record.get(0);
				        double lat = Double.parseDouble(record.get(2));
				        double lng = Double.parseDouble(record.get(3));
				        String country = record.get(4);
				        long population = Long.parseLong(record.get(9));
				        
				        City city = new City();
				        city.setName(name);
				        city.setLatitude(lat);
				        city.setLongitude(lng);
				        city.setCountry(country);
				        city.setPopulation(population);
				        
				        persist(city);
					}catch (Exception e) {
						continue;
					}
				}
				
				return true;
			} catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
	}

	private static void persist(City city) {
		Transaction transaction = null;
		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			session.save(city);
			transaction.commit();
		}catch (Exception e) {
			if(transaction!=null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

}
