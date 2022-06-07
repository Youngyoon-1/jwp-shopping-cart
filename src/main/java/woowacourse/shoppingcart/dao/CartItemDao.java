package woowacourse.shoppingcart.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import woowacourse.shoppingcart.domain.CartItem;
import woowacourse.shoppingcart.dto.AddCartItemRequest;
import woowacourse.shoppingcart.dto.UpdateCartItemRequest;
import woowacourse.shoppingcart.exception.InvalidCartItemException;

@Repository
public class CartItemDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public CartItemDao(final DataSource dataSource, final JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Long> findProductIdsByCustomerId(final Long customerId) {
        final String sql = "SELECT product_id FROM cart_item WHERE customer_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("product_id"), customerId);
    }

    public List<Long> findIdsByCustomerId(final Long customerId) {
        final String sql = "SELECT id FROM cart_item WHERE customer_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), customerId);
    }

    public Long findProductIdById(final Long cartId) {
        try {
            final String sql = "SELECT product_id FROM cart_item WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("product_id"), cartId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidCartItemException();
        }
    }

    public void addCartItem(final Long customerId, final AddCartItemRequest addCartItemRequest) {
        final String sql = "INSERT INTO cart_item(customer_id, product_id, quantity, checked)"
                + "VALUES(:customer_id, :product_id, :quantity, :checked)";

        var paramSource = Map.of(
                "customer_id", customerId,
                "product_id", addCartItemRequest.getProductId(),
                "quantity", addCartItemRequest.getQuantity(),
                "checked", addCartItemRequest.getChecked()
        );

        namedParameterJdbcTemplate.update(sql, paramSource);
    }

    public void deleteCartItem(final Long cartItemId) {
        final String sql = "DELETE FROM cart_item WHERE id = ?";

        final int rowCount = jdbcTemplate.update(sql, cartItemId);
        if (rowCount == 0) {
            throw new InvalidCartItemException();
        }
    }

    public List<CartItem> findByCustomerId(long customerId) {
        final var sql = "SELECT * FROM cart_item WHERE customer_id = :customer_id";

        RowMapper<CartItem> rowMapper = (rs, rowNum) -> {
            var id = rs.getLong("id");
            var customer_id = rs.getLong("customer_id");
            var product_id = rs.getLong("product_id");
            var quantity = rs.getInt("quantity");
            var checked = rs.getBoolean("checked");
            return new CartItem(id, customer_id, product_id, quantity, checked);
        };

        return namedParameterJdbcTemplate.query(sql, Map.of("customer_id", customerId), rowMapper);
    }

    public void deleteAllByCustomerId(Long customerId) {
        final String sql = "DELETE FROM cart_item WHERE customer_id = ?";

        jdbcTemplate.update(sql, customerId);
    }

    public void update(long customerId, UpdateCartItemRequest updateCartItemRequest) {
        final String sql = "UPDATE cart_item SET quantity = (:quantity), checked = (:checked) WHERE customer_id = (:customer_id) AND id = (:id)";

        var paramSource = Map.of(
                "id", updateCartItemRequest.getCartId(),
                "customer_id", customerId,
                "quantity", updateCartItemRequest.getQuantity(),
                "checked", updateCartItemRequest.getChecked()
        );

        namedParameterJdbcTemplate.update(sql, paramSource);
    }
}
