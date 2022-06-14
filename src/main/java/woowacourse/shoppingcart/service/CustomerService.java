package woowacourse.shoppingcart.service;

import org.springframework.stereotype.Service;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.customer.Customer;
import woowacourse.shoppingcart.domain.customer.Password;
import woowacourse.shoppingcart.dto.AuthorizedCustomer;
import woowacourse.shoppingcart.dto.ChangePasswordRequest;
import woowacourse.shoppingcart.dto.DeleteCustomerRequest;
import woowacourse.shoppingcart.dto.SignUpRequest;
import woowacourse.shoppingcart.dto.SignUpResponse;
import woowacourse.shoppingcart.exception.InvalidCustomerException;

@Service
public class CustomerService {

    private static final String DUPLICATED_NAME = "[ERROR] 이미 존재하는 사용자 이름입니다.";
    private static final String DUPLICATED_EMAIL = "[ERROR] 이미 존재하는 이메일입니다.";
    private static final String NOT_MATCH_PASSWORD = "[ERROR] 비밀번호가 일치하지 않습니다.";

    private final CustomerDao customerDao;

    public CustomerService(final CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public SignUpResponse signUp(final SignUpRequest signUpRequest) {
        final String name = signUpRequest.getUsername();
        final String email = signUpRequest.getEmail();
        final String password = signUpRequest.getPassword();

        signUpRequest.toCustomer();

        validateDuplicatedName(name);

        validatedDuplicatedEmail(email);

        customerDao.saveCustomer(name, email, password);

        return new SignUpResponse(name, email);
    }

    private void validatedDuplicatedEmail(final String email) {
        if (customerDao.isExistEmail(email)) {
            throw new InvalidCustomerException(DUPLICATED_EMAIL);
        }
    }

    private void validateDuplicatedName(final String name) {
        if (customerDao.isExistName(name)) {
            throw new InvalidCustomerException(DUPLICATED_NAME);
        }
    }

    public void changePassword(final AuthorizedCustomer authorizedCustomer, final ChangePasswordRequest changePasswordRequest) {
        final var customer = authorizedCustomer.toCustomer();

        final var password = new Password(changePasswordRequest.getPassword());
        validateSamePassword(password, customer);

        final var newPassword = new Password(changePasswordRequest.getNewPassword());

        customerDao.updatePassword(customer.getUsername(), newPassword.getPassword());
    }

    private void validateSamePassword(final Password password, final Customer customer) {
        if (!customer.isSamePassword(password)) {
            throw new InvalidCustomerException(NOT_MATCH_PASSWORD);
        }
    }

    public void deleteUser(final AuthorizedCustomer authorizedCustomer, final DeleteCustomerRequest deleteCustomerRequest) {
        final var password = new Password(deleteCustomerRequest.getPassword());

        final var customer = authorizedCustomer.toCustomer();

        validateSamePassword(password, customer);

        customerDao.deleteByName(customer.getUsername());
    }
}
