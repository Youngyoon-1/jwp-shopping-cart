package woowacourse.shoppingcart.domain;

import java.util.Objects;

public class CartItem {

    private final Long cartItemId;
    private Long productId;
    private int quantity;
    private boolean checked;

    public CartItem(final Long cartItemId, final Long productId, final int quantity, final boolean checked) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.checked = checked;
    }

    public CartItem(final Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean getChecked() {
        return checked;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity && checked == cartItem.checked && Objects.equals(cartItemId,
                cartItem.cartItemId) && Objects.equals(productId, cartItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartItemId, productId, quantity, checked);
    }
}
