package org.samcrow.markedantrecorder;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.samcrow.markedantrecorder.data.ColorDataSet;
import org.samcrow.markedantrecorder.data.ColorDataSet.Color;
import org.samcrow.markedantrecorder.data.ColorDataSet.ColorChangeListener;
import org.samcrow.markedantrecorder.io.RecorderFileInterface;
import org.samcrow.markedantrecorder.io.RecorderFileInterface.InOut;
import org.samcrow.markedantrecorder.util.AlertDialogFragment;

import java.io.IOException;

public class EntryActivity extends AppCompatActivity implements ColorChangeListener {

	private ColorDataSet colors = new ColorDataSet();

	private Button currentEntry1;
	private Button currentEntry2;
	private Button currentEntry3;

	private EditText experimentIdField;

	private RadioButton inRadio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);

		currentEntry1 = (Button) findViewById(R.id.currentEntry1);
		currentEntry2 = (Button) findViewById(R.id.currentEntry2);
		currentEntry3 = (Button) findViewById(R.id.currentEntry3);

		experimentIdField = (EditText) findViewById(R.id.experimentId);

		inRadio = (RadioButton) findViewById(R.id.inRadio);

		colors.addListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}

	@Override
	public void colorsChanged(Color color1, Color color2, Color color3) {
		// set drawables

		Drawable drawable1 = buttonForColor(color1);
		Drawable drawable2 = buttonForColor(color2);
		Drawable drawable3 = buttonForColor(color3);

		currentEntry1.setBackground(drawable1);
		currentEntry2.setBackground(drawable2);
		currentEntry3.setBackground(drawable3);
	}

	public void clearButtonClicked(View view) {
		colors.clear();
	}

	public void enterButtonClicked(View view) {
		if (!colors.isFull()) {
			new AlertDialog.Builder(this)
					.setMessage(R.string.error_enter_three_colors)
					.setNeutralButton(android.R.string.ok, null)
					.show();
			return;
		}
		String experimentId = experimentIdField.getText().toString();
		if (experimentId.isEmpty()) {
			AlertDialogFragment.newInstance(R.string.error_enter_experiment_ID).show(getFragmentManager(), "dialog");
			return;
		}
		if (experimentId.contains("/")) {
			AlertDialogFragment.newInstance("Experiment ID must not contain a slash character. Consider using a dash instead.").show(getFragmentManager(), "dialog");
			return;
		}

		//Get the ant direction
		InOut direction;
		if (inRadio.isChecked()) {
			direction = InOut.In;
		} else {
			direction = InOut.Out;
		}

		try {
			RecorderFileInterface.writeEvent(experimentId, direction, colors);
		} catch (IOException e) {
			e.printStackTrace();
			new AlertDialog.Builder(this)
					.setTitle("Failed to save")
					.setMessage(e.getLocalizedMessage())
					.show();
		}
		colors.clear();
	}

	public void pinkButtonClicked(View view) {
		colors.addColor(Color.Pink);
	}

	public void blueButtonClicked(View view) {
		colors.addColor(Color.Blue);
	}

	public void yellowButtonClicked(View view) {
		colors.addColor(Color.Yellow);
	}

	public void orangeButtonClicked(View view) {
		colors.addColor(Color.Orange);
	}

	public void greenButtonClicked(View view) {
		colors.addColor(Color.Green);
	}

	public void blankButtonClicked(View view) {
		colors.addColor(Color.Missing);
	}

	private Drawable buttonForColor(Color color) {

		if (color == null) {
			return getResources().getDrawable(R.drawable.empty_color_button);
		}

		switch (color) {
			case Violet:
				return getResources().getDrawable(R.drawable.purple_button);
			case Pink:
				return getResources().getDrawable(R.drawable.pink_button);
			case Blue:
				return getResources().getDrawable(R.drawable.blue_button);
			case White:
				return getResources().getDrawable(R.drawable.white_button);
			case Gold:
				return getResources().getDrawable(R.drawable.gold_button);
			case Orange:
				return getResources().getDrawable(R.drawable.orange_button);
			case Green:
				return getResources().getDrawable(R.drawable.green_button);
			case Yellow:
				return getResources().getDrawable(R.drawable.yellow_button);
			case Missing:
				return getResources().getDrawable(R.drawable.blank_button);
		}
		throw new UnsupportedOperationException("Color " + color + " does not have a known corresponding drawable");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("colors")) {
			colors = (ColorDataSet) savedInstanceState.get("colors");
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("colors", colors);
	}
}
