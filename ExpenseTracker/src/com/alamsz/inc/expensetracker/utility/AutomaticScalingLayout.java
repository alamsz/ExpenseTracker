package com.alamsz.inc.expensetracker.utility;

import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AutomaticScalingLayout {
	/**
	 * Scales the contents of the given view so that it completely fills the
	 * given container on one axis (that is, we're scaling isotropically).
	 * 
	 * @param rootView
	 *            The view that contains the interface elements
	 * @param container
	 *            The view into which the interface will be scaled
	 */
	public void scaleContents(View rootView) {
		float xScale = 1.0f;
		float yScale = 1.0f;
		// Determine screen size
		if ((rootView.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			xScale= 2.0f;
			yScale = 2.0f;

		}else if ((rootView.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			xScale= 1.5f;
			yScale = 1.5f;

		} else if ((rootView.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			
		} else if ((rootView.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			xScale= 0.75f;
			yScale = 0.75f;

		} else {
			
		}

		// Compute the scaling ratio. Note that there are all kinds of games you
		// could
		// play here - you could, for example, allow the aspect ratio to be
		// distorted
		// by a certain percentage, or you could scale to fill the *larger*
		// dimension
		// of the container view (useful if, for example, the container view can
		// scroll).
		float scale = Math.min(xScale, yScale);

		// Scale our contents
		scaleViewAndChildren(rootView, scale);
	}

	/**
	 * Scale the given view, its contents, and all of its children by the given
	 * factor.
	 * 
	 * @param root
	 *            The root view of the UI subtree to be scaled
	 * @param scale
	 *            The scaling factor
	 */
	public static void scaleViewAndChildren(View root, float scale) {
		// Retrieve the view's layout information
		ViewGroup.LayoutParams layoutParams = root.getLayoutParams();

		// Scale the view itself
		if (layoutParams.width != ViewGroup.LayoutParams.FILL_PARENT
				&& layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
			layoutParams.width *= scale;
		}
		if (layoutParams.height != ViewGroup.LayoutParams.FILL_PARENT
				&& layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
			layoutParams.height *= scale;
		}

		// If this view has margins, scale those too
		if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
			marginParams.leftMargin *= scale;
			marginParams.rightMargin *= scale;
			marginParams.topMargin *= scale;
			marginParams.bottomMargin *= scale;
		}

		// Set the layout information back into the view
		root.setLayoutParams(layoutParams);

		// Scale the view's padding
		root.setPadding((int) (root.getPaddingLeft() * scale),
				(int) (root.getPaddingTop() * scale),
				(int) (root.getPaddingRight() * scale),
				(int) (root.getPaddingBottom() * scale));

		// If the root view is a TextView, scale the size of its text. Note that
		// this is not quite precise -
		// it appears that text can't be exactly scaled to any desired size,
		// presumably due to limitations
		// of the font system. You may have to make your fonts a little bit
		// smaller than you otherwise might
		// in order to make sure that the text will always fit at any scaling
		// factor.
		if (root instanceof TextView) {
			TextView textView = (TextView) root;
			float scaleTextSize = textView.getTextSize() * scale;

			textView.setTextSize(scaleTextSize);
			/*int scaleTextWidth = Math.round(textView.getWidth() * scale);
			textView.setWidth(scaleTextWidth);
			int scaleTextHeight = Math.round(textView.getHeight() * scale);
			textView.setHeight(scaleTextHeight);*/
			Log.d("AutomaticScaling",
					"Scaling text size from " + textView.getTextSize() + " to "
							+ scaleTextSize);
			/*Log.d("AutomaticScaling",
					"Scaling text width from " + textView.getWidth() + " to "
							+ scaleTextWidth);
			Log.d("AutomaticScaling",
					"Scaling text height from " + textView.getHeight() + " to "
							+ scaleTextHeight);*/
		}

		if (root instanceof EditText) {
			EditText editText = (EditText) root;
			float scaleEditTextSize = editText.getTextSize() * scale;
			Log.d("Calculator",
					"Scaling text size from " + editText.getTextSize() + " to "
							+ scaleEditTextSize);
			editText.setTextSize(scaleEditTextSize);
			int scaleEditTextWidth = Math.round(editText.getWidth() * scale);
			editText.setWidth(scaleEditTextWidth);
			int scaleEditTextHeight = Math.round(editText.getHeight() * scale);
			editText.setHeight(scaleEditTextHeight);
		}

		// If the root view is a ViewGroup, scale all of its children
		// recursively
		if (root instanceof ViewGroup) {
			ViewGroup groupView = (ViewGroup) root;
			for (int cnt = 0; cnt < groupView.getChildCount(); ++cnt)
				scaleViewAndChildren(groupView.getChildAt(cnt), scale);
		}
	}
}
