package com.infinityicon.newcellphoneprices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Categories extends Activity implements OnItemClickListener {
	ListView lstBrands;
	TextView lblBrands;
	CategoryAdapter adapter;
	final String strBrands[] = new String[] { "Nokia", "Samsung", "Blackberry",
			"SonyEricsson", "HTC", "Apple", "LG", "Sony", "Huawei",
			"Panasonic", "Lenovo", "Motorola", "QMobile", "Voice", "GFive",
			"ZTE", "Micromax", "Lava" };
	final int[] iImages = { R.drawable.nokia, R.drawable.samsung,
			R.drawable.blackberry, R.drawable.sonyericsson, R.drawable.htc,
			R.drawable.apple, R.drawable.lg, R.drawable.sony,
			R.drawable.huawei, R.drawable.panasonic, R.drawable.lenovo,
			R.drawable.motorola, R.drawable.qmobile, R.drawable.voice,
			R.drawable.gfive, R.drawable.zte, R.drawable.micromax,
			R.drawable.lava };

	// int iBrandIDs[] = new int[] { 1, 2, 3, 4, 5, 6, 7 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories);

		// lblBrands = ( TextView ) findViewById( R.id.lblBrands );
		// lblBrands.setText( R.string.strPoweredBy);
		// Linkify.addLinks( lblBrands, Linkify.ALL );

		lstBrands = (ListView) findViewById(R.id.lstBrands);
		adapter = new CategoryAdapter(Categories.this,
				R.layout.categories_layout, strBrands);
		lstBrands.setAdapter(adapter);
		lstBrands.setTextFilterEnabled(true);
		lstBrands.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// String strSelected = ((TextView) view).getText().toString();
		TextView tvBrand = (TextView) view.findViewById(R.id.textView1);
		String strSelected = tvBrand.getText().toString();
		Toast.makeText(getApplicationContext(),
				"Loading " + strSelected + " Phones. Please Wait!",
				Toast.LENGTH_SHORT).show();
		Intent intent;
		intent = new Intent(this, MainActivity.class);
		intent.putExtra("Phone", strSelected);
		Log.e("Categories:", "Posted " + strSelected + " in putExtra.");
		startActivity(intent);
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

	public class CategoryAdapter extends ArrayAdapter<String> {

		public CategoryAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.categories_layout, null);
			}
			// //Log.d ( "Categories", "GetView above display");
			String brand = strBrands[position];
			int imgid = iImages[position];
			((TextView) row.findViewById(R.id.textView1)).setText(brand);
			ImageView img = (ImageView) row.findViewById(R.id.imageView1);
			img.setImageResource(imgid);
			// Log.d ( "Categories", "Row: "+ row);
			return row;
		}
	}
	public class CategoryAdapter2 extends ArrayAdapter<String> {

		public CategoryAdapter2(Context context, int resource,
				int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
		}
	}
}