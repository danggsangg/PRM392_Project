package com.example.productclasswork.products;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.productclasswork.DbHelper;
import com.example.productclasswork.R;

public class EditProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    EditText txtTitle, txtPrice, txtStock, txtDescription;
    Button btnSave, btnChooseImage;
    ImageView imgPreview;
    Uri imageUri;
    DbHelper db;
    int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        db = new DbHelper(this);
        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtStock = findViewById(R.id.txtStock);
        txtDescription = findViewById(R.id.txtDescription);
        btnSave = findViewById(R.id.btnSave);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        imgPreview = findViewById(R.id.imgPreview);

        // Nếu là sửa sản phẩm
        Intent i = getIntent();
        if (i.hasExtra("product")) {
            Product p = (Product) i.getSerializableExtra("product");
            productId = p.id;
            txtTitle.setText(p.title);
            txtPrice.setText(String.valueOf(p.price));
            txtStock.setText(String.valueOf(p.stock));
            txtDescription.setText(p.description);

            if (p.image != null && !p.image.isEmpty()) {
                imageUri = Uri.parse(p.image);
                imgPreview.setImageURI(imageUri);
            }
        }

        // Chọn ảnh từ thư viện
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Lưu dữ liệu sản phẩm
        btnSave.setOnClickListener(v -> {
            String imagePath = (imageUri != null) ? imageUri.toString() : "";

            Product p = new Product(
                    productId,
                    txtTitle.getText().toString(),
                    Double.parseDouble(txtPrice.getText().toString()),
                    Integer.parseInt(txtStock.getText().toString()),
                    txtDescription.getText().toString(),
                    imagePath
            );

            if (productId == -1) {
                db.insertProduct(p);
            } else {
                db.updateProduct(p);
            }

            setResult(RESULT_OK);
            finish();
        });
    }

    // Nhận kết quả chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgPreview.setImageURI(imageUri);
        }
    }
}
