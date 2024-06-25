package com.infinityicon.newcellphoneprices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener {
	TextView lblBrand;
	ListView lstPhones;
	ProgressBar pb;

	String URL;
	String strBrand; // coming from category

	DrawableManager dm;

	CellPhoneData cellPhoneData;
	Cursor cursor;

	SimpleCursorAdapter cursorAdapter;
	Intent intFetchCellPhones; // to take to update page if no item found in
								// list item

	// //////////////////////////////////////////

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lblBrand = (TextView) findViewById(R.id.lblBrand);
		lstPhones = (ListView) findViewById(R.id.lstPhones);

		pb = (ProgressBar) findViewById(R.id.progressBar);
		pb.setProgress(25);

		String strTitle = (getIntent().getExtras().getString("Phone"));
		lblBrand.setText(strTitle + " Mobile Prices");
		strBrand = getIntent().getExtras().getString("Phone");

		cellPhoneData = new CellPhoneData(MainActivity.this);

		cursor = cellPhoneData.SelectByBrand(strBrand);
		if (cursor.getCount() == 0) {
			cursor.close();
			cellPhoneData.CloseDB();

			intFetchCellPhones = new Intent(this, FetchCellPhones.class);
			intFetchCellPhones.putExtra("strFrom", "FirstTime.class");
			startActivity(intFetchCellPhones);

			// Toast.makeText(
			// MainActivity.this,
			// "Please click 'Get Phones and Prices' Button "
			// + "to get latest Phones and Prices.",
			// Toast.LENGTH_LONG).show();
		} else {
			// dm = new DrawableManager ( );
			cursorAdapter = new SimpleCursorAdapter(MainActivity.this,
					R.layout.phone_display_layout, cursor, new String[] {
							cellPhoneData.C_NAME, cellPhoneData.C_PRICE,
							cellPhoneData.C_IMAGE, CellPhoneData.C_OS,
							CellPhoneData.C_CORE }, new int[] {
							R.id.txtPhoneName, R.id.txtPhonePrice,
							R.id.imgPhone, R.id.txtPhoneOS, R.id.txtPhoneCore });
			pb.setProgress(50);
			// cursorAdapter = new SimpleCellPhoneAdapter (MainActivity.this,
			// R.layout.phone_display_layout, cursor, new String [ ] {
			// cellPhoneData.C_NAME, cellPhoneData.C_PRICE,
			// cellPhoneData.C_IMAGE },
			// new int [ ] { R.id.txtPhoneName, R.id.txtPhonePrice,
			// R.id.imgPhone }
			// );
			cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

				@Override
				public boolean setViewValue(View view, Cursor cursor,
						int columnIndex) {
					switch (view.getId()) {
					case R.id.txtPhoneName:
						String strName = cursor.getString(cursor
								.getColumnIndex(cellPhoneData.C_NAME));
						((TextView) view).setText(strName);
						return true;

					case R.id.imgPhone:
						String strImage = cursor.getString(cursor
								.getColumnIndex(cellPhoneData.C_IMAGE));
						// dm.fetchDrawableOnThread( strImage, ((ImageView)
						// view));
						((ImageView) view).setImageBitmap(BitmapFactory
								.decodeFile(strImage));
						// Log.d ( "MainActivity", "images/" + strImage);
					default:
						return false;
					}
				}
			});
			pb.setProgress(75);
			lstPhones.setAdapter(cursorAdapter);
			pb.setProgress(100);

			lstPhones.setOnItemClickListener(this);
			//cursor.close();
			cellPhoneData.CloseDB();
		}
	}

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

	class SimpleCellPhoneAdapter extends SimpleCursorAdapter {
		@SuppressWarnings("deprecation")
		public SimpleCellPhoneAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			super.bindView(view, context, cursor);
		}

		public View newView() {
			return lblBrand;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		TextView tvTitle = ((TextView) view.findViewById(R.id.txtPhoneName));
		String strPhoneName = tvTitle.getText().toString();

		String strPhoneURL = strPhoneName.replace(" ", "-");
		strPhoneURL = strPhoneURL.toLowerCase();
		strPhoneURL = "http://www.newcellphoneprices.com/" + strPhoneURL
				+ "-price-and-specs.html";

		Toast.makeText(this,
				"Opening " + strPhoneName + " Specs. Please Wait!",
				Toast.LENGTH_LONG).show();

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strPhoneURL));
		startActivity(intent);
		// Linkify. ( strPhoneName, Linkify.ALL );
	}

	// class CellPhoneAdapter extends ArrayAdapter<CellPhone> {
	//
	// public CellPhoneAdapter(Context context, int textViewResourceId,
	// List<CellPhone> objects) {
	// super(context, textViewResourceId, objects);
	// // TODO Auto-generated constructor stub
	// }
	//
	// CellPhoneAdapter() {
	// super(MainActivity.this, android.R.layout.simple_list_item_1,
	// cellPhoneList);
	// }

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// View row = convertView;
	// if (row == null) {
	// LayoutInflater inflater = getLayoutInflater();
	// row = inflater.inflate(R.layout.phone_display_layout, null);
	// }
	//
	// CellPhone c = cellPhoneList.get(position);
	// ((TextView) row.findViewById(R.id.txtPhoneName)).setText(c
	// .getCellName());
	// ((TextView) row.findViewById(R.id.txtPhonePrice)).setText(c
	// .getCellDesc());
	// imgPhone = (ImageView) row.findViewById(R.id.imgPhone);
	// // imgPhone.setImageURI( Uri.parse(
	// //
	// "http://www.newcellphoneprices.com/Mobile_Set_Images/huawei-ascend-w1.jpg")
	// // );
	// // imgPhone.setImageURI( Uri.parse(
	// //
	// "http://www.newcellphoneprices.com/Mobile_Set_Images/huawei-ascend-w1.jpg")
	// // );
	// // Drawable d = LoadImageFromUrlWeb (
	// //
	// "http://www.newcellphoneprices.com/Mobile_Set_Images/huawei-ascend-w1.jpg"
	// // );
	// DrawableManager dm = new DrawableManager();
	// dm.fetchDrawableOnThread(c.getCellImage(), imgPhone);
	// return row;
	// }
	// }
}