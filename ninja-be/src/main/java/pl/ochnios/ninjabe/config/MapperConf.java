package pl.ochnios.ninjabe.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ochnios.ninjabe.model.mappers.ChatOptionsMapper;
import pl.ochnios.ninjabe.model.mappers.ConversationMapper;
import pl.ochnios.ninjabe.model.mappers.MessageMapper;
import pl.ochnios.ninjabe.model.mappers.PageMapper;
import pl.ochnios.ninjabe.model.mappers.UserMapper;

@Configuration
public class MapperConf {

    @Bean
    public PageMapper pageMapper() {
        return Mappers.getMapper(PageMapper.class);
    }

    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public ConversationMapper conversationMapper() {
        return Mappers.getMapper(ConversationMapper.class);
    }

    @Bean
    public MessageMapper messageMapper() {
        return Mappers.getMapper(MessageMapper.class);
    }

    @Bean
    public ChatOptionsMapper chatOptionsMapper() {
        return Mappers.getMapper(ChatOptionsMapper.class);
    }
}
