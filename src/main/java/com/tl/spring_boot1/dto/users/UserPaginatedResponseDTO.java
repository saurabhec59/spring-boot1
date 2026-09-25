package com.tl.spring_boot1.dto.users;
import com.tl.spring_boot1.model.User;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class UserPaginatedResponseDTO {
    private List<UserResponse> content; // This will be sent to client and will contain fields only what server wants to expose, and notice it is 'UserResponse' not 'User'.
    private Integer page;
    private Integer size;
    private Integer totalElements;
    private Integer totalPages;
    private boolean hasNext;

    // this constructor taking List of users of type 'UserResponse' and initializing the same in 'content' field, not 'User' directly returned from db.
    private UserPaginatedResponseDTO( List<UserResponse> content, Integer page, Integer size, Integer totalElements, Integer totalPages, boolean hasNext ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
    }

    public static UserPaginatedResponseDTO from(Page<User> page){
        // repo returned page.getContent() contains all fields which we do not want to expose, so we converted that list into 'UserResponse' by doing -> getUsersList( page.getContent() )
        UserPaginatedResponseDTO userPaginatedResponseDTO = new UserPaginatedResponseDTO( getUsersList(page.getContent()), page.getNumber() + 1, page.getSize(), (int)page.getTotalElements(), page.getTotalPages(), page.hasNext() );
        return userPaginatedResponseDTO;
    }

    // this method will take List of all users 'page.getContent()' returned from db and returns the List of type 'UserResponse'
    private static List<UserResponse> getUsersList( List<User> allUsers){
        List<UserResponse> content = new ArrayList<>();
        for( User user : allUsers){
            content.add( UserResponse.from(user) );
        }
        return content;
    }



    // getters to that jackson can get fields and serialize the response. 'allUsers' should not be exposed
    public List<UserResponse> getContent(){ return content; }
    public Integer getPage() { return page; }
    public Integer getSize() { return size; }
    public Integer getTotalElements() { return totalElements; }
    public Integer getTotalPages() { return totalPages; }
    public boolean isHasNext() { return hasNext; }
}

