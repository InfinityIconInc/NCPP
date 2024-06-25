package com.infinityicon.newcellphoneprices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FetchCellPhones extends Activity {

	final String strURL = "http://www.newcellphoneprices.com/c6.php?c_id=0";
	final String MOBILE_IMAGE_SITE_PATH = "http://www.newcellphoneprices.com/Mobile_Set_Images/";
	final String MOBILE_IMAGE_LOCAL_PATH = "/data/data/com.infinityicon.newcellphoneprices/images/";
	DownloadImages di;
	List<String> localFiles;
	// /////Elements in C5.php.xml file//////////
	final String KEY_ID = "id";
	final String KEY_ITEM = "item";
	final String KEY_NAME = "title";
	final String KEY_IMAGE = "image";
	final String KEY_DESC = "description";
	final String KEY_BRAND = "brand";
	final String KEY_CORE = "core";
	final String KEY_OS = "os";
	// / //////////////////////////////////////////
	final String TAG = "FetchCellPhones";

	ProgressBar pbProgress;
	TextView txtFatchTitle;
	TextView txtMessage;
	TextView txtNote;
	Button btnDone;
	ProgressBar pbBig;

	List<CellPhone_OS_Core> cellPhoneList;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.op_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_Fetch:
			Intent fetchIntent = new Intent(this, FetchCellPhones.class);
			fetchIntent.putExtra("strFrom", "FetchCellPhones.class");
			startActivity(fetchIntent);
			return true;

		case R.id.menu_Brand:
			Intent brandIntent = new Intent(this, Categories.class);
			brandIntent.putExtra("strFrom", "MainActivity.class");
			startActivity(brandIntent);
			return true;

		default:
			return false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fetch_cellphones);

		// setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

		pbProgress = (ProgressBar) findViewById(R.id.pbFetch);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		txtNote = (TextView) findViewById(R.id.txtNote);
		btnDone = (Button) findViewById(R.id.btnDone);
		txtFatchTitle = (TextView) findViewById(R.id.txtFatchTitle);
		pbBig = (ProgressBar) findViewById(R.id.pb);

		pbBig.setVisibility(View.INVISIBLE);

		int isFirstTime = 0;
		Bundle b = getIntent().getExtras();

		if (b.getString("strFrom").compareTo("FirstTime.class") == 0)
			isFirstTime = 1;

		// Log.d ( TAG, "FirstTime: " + getIntent ( ).getExtras().getString(
		// "strEmpty") + " " + isFirstTime );

		if (isFirstTime == 1) {
			txtFatchTitle.setText("First Time Update!");
			btnDone.setText("Update Prices and Phones (4MB)");
			txtMessage.setText("Important Note!");
			txtNote.setText("Phones and Prices are NOT UP-TO-DATE. "
					+ "Please Press the Button Below to Update app for Latest Phones and Prices. "
					+ "Fetching new phones and prices may take several minutes, "
					+ "depending on the connection speed. We recommand using the WiFi connection "
					+ "as on cell phone network it may incurr a lot of charges and time."
					+ " Click the button below to fetch and update New Phones and Prices.");
		} else {
			txtFatchTitle.setText("Update Data");
			btnDone.setText("Fetch New Prices (2MB)");
			txtMessage.setText("Important Note!");
			txtNote.setText("Fetching new phones and prices may take several minutes, "
					+ "depending on the connection speed. We recommand using the WiFi connection "
					+ "as on cell phone network it may incurr a lot of charges and time."
					+ " Click the button below to fetch and update New Phones and Prices.");
		}

		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				txtFatchTitle.setText("Updating Prices and Phones");
				txtMessage.setText("Starting Update!");
				new Fetch().execute(strURL);
				btnDone.setVisibility(View.INVISIBLE);
				txtNote.setText("Sit-back and Relax while the fresh data is being installed.");
				pbBig.setVisibility(View.VISIBLE);

			}
		});
	}
	
	class InsertLocal extends AsyncTask <List<CellPhone_OS_Core>, Integer, String> {

		@Override
		protected String doInBackground(List<CellPhone_OS_Core>... params) {
			CellPhoneData cpd = new CellPhoneData ( FetchCellPhones.this );
			cpd.zap();
			int i = 0;
			for ( List<CellPhone_OS_Core> c : params ) {
				//Log.d ( TAG, "NewAS: " + c.size());
				pbProgress.setMax( c.size());
				for ( CellPhone_OS_Core co : c ) {
					Log.d ( TAG, "InsLcl: " + co.getCellName());
					cpd.insert( co );
					publishProgress ( i++ );
				}
			}
			cpd.CloseDB();
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			txtFatchTitle.setText("Congratulations!");
			txtMessage.setText("Data Updated!");
			txtNote.setText("Prices and Data is Up-to-Date now. Thanks for Updating!");

		btnDone.setText("Done");
		pbBig.setVisibility(View.INVISIBLE);

		btnDone.setVisibility(View.VISIBLE);
		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent doneIntent = new Intent(FetchCellPhones.this,
						Categories.class);
				doneIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				doneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(doneIntent);
			}
		});

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			pbProgress.setProgress(values[0]);
		}
	}
	
	class Fetch extends AsyncTask<String, Integer, List<CellPhone_OS_Core>> {
		XMLParser parser;
		NodeList nl;
		float totalItems;
		float progressedItems;
		int progressResult;

		File imageFile;
		AssetManager assetManager;
		String[] assetsList;

		@Override
		protected List<CellPhone_OS_Core> doInBackground(String... URLparams) {
			cellPhoneList = new ArrayList<CellPhone_OS_Core>();
			// //For Downloading XML and Images Progress Variables////
			publishProgress(25);// ////////////////////////////10%
			progressResult = 0;
			totalItems = 0f;
			// //////////////////////

			di = new DownloadImages();
			assetManager = getAssets();
			localFiles = di.ListLocalFiles(); // LocallySavedFilesList

			try {
				assetsList = assetManager.list("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			publishProgress(50);// /////////////////////////25%
			try {
				parser = new XMLParser();
				String xml = parser.getXMLFromURL(URLparams[0]);
				Document doc = parser.getDomElement(xml);
				nl = doc.getElementsByTagName(KEY_ITEM);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			publishProgress(100);// ///////////////////////////75%
			// ////ASSETS COPYY////////////
			pbProgress.setMax(assetsList.length);
			int localFilesCounter = 0;
			//publishProgress(localFilesCounter);
			if (localFiles.size() < 100) {
				for (String strFN : assetsList) {
					publishProgress(localFilesCounter++);
					//Log.d(TAG, "CopyingFile: " + strFN);
					CopyImageFromAssets(strFN);
				}
			}
			// //////////////////////////////
			pbProgress.setMax(100);// resetting the progressbar to 100 max
			try {
				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);
					// Log.e ( "XML Data", parser.getValue(e, KEY_NAME));
					// Log.e ( "XML Data", parser.getValue(e, KEY_IMAGE ));
					// Log.e("XML Data", parser.getValue(e, KEY_DESC));
					if (Integer.parseInt(parser.getValue(e, KEY_ID)) == -1) {
						// #ofRows are stored in itemID -1
						totalItems = Integer.parseInt(parser.getValue(e, KEY_DESC));
						progressedItems = 0f;
						continue;
					}
					// //Log.d(TAG, "B4 Adding Parsed Data in Object");
					// Adding Parsed Data in CellPhone object
					// Log.d (TAG, parser.getValue(e,
					// KEY_DESC).trim().replace("\\n", "\n") );
					String strPrice = parser.getValue(e, KEY_DESC).trim()
							.replace(" ", "\n");
					strPrice = strPrice.replace(":", ": ");
					cellPhoneList.add(new CellPhone_OS_Core(Integer.parseInt(parser
							.getValue(e, KEY_ID)), parser.getValue(e, KEY_NAME)
							.trim(), strPrice, parser
							.getValue(e, KEY_IMAGE)
							.trim()
							.replace(MOBILE_IMAGE_SITE_PATH,
									MOBILE_IMAGE_LOCAL_PATH), parser.getValue(e,
							KEY_BRAND), parser.getValue(e, KEY_CORE), parser
							.getValue(e, KEY_OS)));
					progressedItems++; // one item more progressed/processed

					for (int c = 0; c < localFiles.size(); c++) {
						if (parser.getValue(e, KEY_IMAGE)
								.replace(MOBILE_IMAGE_SITE_PATH, "")
								.compareTo(localFiles.get(c)) == 0) {
							localFiles.remove(c);
						}
						// remove the local file entry in localFiles<list> if file
						// needed to b displayed
					}
					di.DownloadFromURL(parser.getValue(e, KEY_IMAGE), (parser
							.getValue(e, KEY_IMAGE).replace(MOBILE_IMAGE_SITE_PATH,
							"")));
					progressResult = Math
							.round((progressedItems / totalItems) * 100);
					publishProgress(progressResult);
					// /////Log.d(TAG, "Progressed Items: " + progressResult);
				}
				// //Log.d ( TAG, "B4 Removing File");
				for (String strDelFN : localFiles) {
					di.DeleteLocalFile(strDelFN);
					//Log.d(TAG, "DEL: " + strDelFN);
				}
			} catch (Exception e) {
				//Log.d ( TAG, "Internet Not Connected");
				e.printStackTrace();
			}
			// ////Log.e("XML:", "XML Parsed " + totalItems);
			return cellPhoneList;
		}

		@Override
		protected void onPostExecute(final List<CellPhone_OS_Core> result) {
			super.onPostExecute(result);
			// Adding Fetched Data to DB//
			// Accessing Global Variable for DB
			// ///Log.d(TAG, "Inside Post Execute");
			// CellPhoneData cellPhoneData = ((CellPhoneApp)
			// getApplication()).cellPhoneData;
			if (result.isEmpty()) {
				txtFatchTitle.setText("Error!");
				txtMessage.setText("Internet Not Connected...");
				txtNote.setText("Internet connection is required to "
						+ "Update the Data! Please Connect to WiFi or Data "
						+ "Connection to Update.");

				pbBig.setVisibility(View.INVISIBLE);

			} else {
				// /////Log.d(TAG, "Application Variable Passed.");
//					CellPhoneData cellPhoneData = new CellPhoneData ( FetchCellPhones.this );
//					cellPhoneData.zap();
//					for (CellPhone_OS_Core c : result) {
//						cellPhoneData.insert(c);
//						Log.d( TAG, "Adding Local: " + c.getCellName() );
//					}
//					Log.d ( TAG, "Thread Loop Processed");
//					cellPhoneData.CloseDB();
				new InsertLocal().execute(result);
//				txtFatchTitle.setText("Congratulations!");
//				txtMessage.setText("Data Updated!");
//				txtNote.setText("Prices and Data is Up-to-Date now. Thanks for Updating!");
			}
//
//			btnDone.setText("Done");
//			pbBig.setVisibility(View.INVISIBLE);
//
//			btnDone.setVisibility(View.VISIBLE);
//			btnDone.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Intent doneIntent = new Intent(FetchCellPhones.this,
//							Categories.class);
//					doneIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					doneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(doneIntent);
//				}
//			});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// super.onProgressUpdate(values);
			pbProgress.setProgress(values[0]);
			// ////////Log.d(TAG, values[0].toString());
			txtMessage.setText("Fetching and Setting-up Data!");
		}

		void CopyImageFromAssets(String strFile) {
			AssetManager am;
			am = getAssets();
			File dirImages = new File(MOBILE_IMAGE_LOCAL_PATH);
			File imageFile;
			if (!dirImages.exists())
				dirImages.mkdir();

			imageFile = new File(MOBILE_IMAGE_LOCAL_PATH + strFile);

			if (!imageFile.exists()) {
				InputStream in = null;
				OutputStream op = null;

				try {
					in = am.open(strFile);
					op = new FileOutputStream(MOBILE_IMAGE_LOCAL_PATH + strFile);
					byte[] buffer = new byte[1024]; // Copying Assets Bytes
													// to Internal
													// Mem////////////
					int iRead;

					while ((iRead = in.read(buffer)) != -1) {
						//Log.d(TAG, "Copying Local Files: " + strFile);
						op.write(buffer, 0, iRead);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		void CopyAssets(String strFile) {
			AssetManager am = getAssets();
			String[] assetFiles = null;
			File dirImages = new File(MOBILE_IMAGE_LOCAL_PATH);
			File imageFile;
			if (!dirImages.exists())
				dirImages.mkdir();

			try {
				assetFiles = am.list("");
				// copyTotalItems = assetFiles.length +1;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (String assetFile : assetFiles) {
				// ///////Log.d(TAG, "assetFile: " + assetFile);

				imageFile = new File(MOBILE_IMAGE_LOCAL_PATH + assetFile);

				if (!imageFile.exists()) {
					InputStream in = null;
					OutputStream op = null;

					try {
						in = am.open(assetFile);
						op = new FileOutputStream(MOBILE_IMAGE_LOCAL_PATH
								+ assetFile);
						byte[] buffer = new byte[1024]; // Copying Assets Bytes
														// to Internal
														// Mem////////////
						int iRead;

						while ((iRead = in.read(buffer)) != -1) {
							// //////Log.d(TAG, "Copying Local Files: " +
							// assetFile);
							op.write(buffer, 0, iRead);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}
}