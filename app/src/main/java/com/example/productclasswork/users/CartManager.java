package com.example.productclasswork.users;

import com.example.productclasswork.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<CartItem> cart = new ArrayList<>();

    public static void addToCart(CartItem item) {
        for (CartItem c : cart) {
            if (c.productId == item.productId) {
                if (c.quantity + item.quantity <= c.stock) {
                    c.quantity += item.quantity;
                } else {
                    c.quantity = c.stock; // tối đa là stock
                }
                return;
            }
        }
        cart.add(item);
    }

    public static List<CartItem> getCart() {
        return new ArrayList<>(cart);
    }

    public static void removeItem(CartItem item) {
        cart.remove(item);
    }

    public static void removeItems(List<CartItem> items) {
        cart.removeAll(items);
    }

    public static void clearCart() {
        cart.clear();
    }

    public static double getTotal() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.price * item.quantity;
        }
        return total;
    }
}
