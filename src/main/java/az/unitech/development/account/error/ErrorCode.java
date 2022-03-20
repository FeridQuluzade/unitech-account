package az.unitech.development.account.error;

public interface ErrorCode {

    ErrorCode UNKNOWN_ERROR_CODE = UnknownErrorCode.INSTANCE;

    String code();

    enum UnknownErrorCode implements ErrorCode {
        INSTANCE;

        @Override
        public String code() {
            return "unknown";
        }
    }

}