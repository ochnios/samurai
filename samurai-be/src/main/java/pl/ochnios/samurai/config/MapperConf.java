package pl.ochnios.samurai.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ochnios.samurai.model.mappers.ChatOptionsMapper;
import pl.ochnios.samurai.model.mappers.ConversationMapper;
import pl.ochnios.samurai.model.mappers.DocumentChunkMapper;
import pl.ochnios.samurai.model.mappers.DocumentMapper;
import pl.ochnios.samurai.model.mappers.FileMapper;
import pl.ochnios.samurai.model.mappers.MessageMapper;
import pl.ochnios.samurai.model.mappers.PageMapper;
import pl.ochnios.samurai.model.mappers.UserMapper;

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

    @Bean
    public FileMapper fileMapper() {
        return Mappers.getMapper(FileMapper.class);
    }

    @Bean
    public DocumentMapper documentMapper() {
        return Mappers.getMapper(DocumentMapper.class);
    }

    @Bean
    public DocumentChunkMapper documentChunkMapper() {
        return Mappers.getMapper(DocumentChunkMapper.class);
    }
}
