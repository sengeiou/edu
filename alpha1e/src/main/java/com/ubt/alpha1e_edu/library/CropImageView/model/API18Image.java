package com.ubt.alpha1e_edu.library.CropImageView.model;

import android.graphics.Matrix;

import com.ubt.alpha1e_edu.library.CropImageView.CropImageView;

public class API18Image extends CropImage {

  API18Image(CropImageView imageView) {
    super(imageView);
  }

  @Override
  public Matrix getMatrix() {
    return cropImageView.getImageMatrix();
  }
}