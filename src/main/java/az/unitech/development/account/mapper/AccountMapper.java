package az.unitech.development.account.mapper;

import az.unitech.development.account.dto.AccountDto;
import az.unitech.development.account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto toAccountDto(Account account);

    List<AccountDto> toAccountDtoList(List<Account> accounts);

}