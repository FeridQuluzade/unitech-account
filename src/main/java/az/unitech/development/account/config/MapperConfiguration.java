package az.unitech.development.account.config;

import az.unitech.development.account.mapper.AccountMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public AccountMapper accountMapper() {
        return AccountMapper.INSTANCE;
    }

}