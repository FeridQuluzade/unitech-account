package az.unitech.development.account.error;

public enum ErrorCodes implements ErrorCode {

    ACCOUNT_IS_SAME,
    ACCOUNT_NOT_FOUND,
    ACCOUNT_STATUS_CLOSED,
    NOT_ENOUGH_AMOUNT;

    @Override
    public String code() {
        return this.name();
    }

}
