package com.example.gadgetry.databases;

import com.example.gadgetry.models.ProductData;
import com.example.gadgetry.utils.UtilKeys;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DBHelper {

    private DatabaseReference databaseReference;

    public DBHelper(String tableName) {
          databaseReference = FirebaseDatabase.getInstance().getReference(tableName);
    }

    public Task<Void> insertProduct(ProductData productData) {
        String key = databaseReference.push().getKey(); // Generate a new unique key for the product
        return databaseReference.child(key).setValue(productData); // Store it under the new key
    }

    public Task<DataSnapshot> getProductData(String key) {
        return databaseReference.child(key).get();
    }

    public Query getAllProductsData() {
        return databaseReference.orderByKey();
    }

    // Delete a product by key
    public Task<Void> deleteProduct(String key) {
        return databaseReference.child(key).setValue(null); // Set the product to null to delete it
    }

  public Task<Void> insertOrder(ProductData productData) {
        String key = databaseReference.push().getKey();
        return databaseReference.child(key).setValue(productData);
    }

    public Query getAllOrderData() {
        return databaseReference.orderByKey(); // Fetch all orders if implemented similarly
    }

    // Delete order by key
    public Task<Void> deleteOrder(String key) {
        return databaseReference.child(key).setValue(null);
    }

    // Delete all orders if needed
    public Task<Void> deleteOrderAll() {
        return databaseReference.setValue(null); // Delete all products (or orders) from the Products node
    }
}
