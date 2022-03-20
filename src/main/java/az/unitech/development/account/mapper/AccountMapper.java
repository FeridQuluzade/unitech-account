package az.unitech.development.account.mapper;

import az.unitech.development.account.dto.AccountDto;
import az.unitech.development.account.model.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto toAccountDto(Account account);

    List<AccountDto> toAccountDtoList(List<Account> accounts);

}