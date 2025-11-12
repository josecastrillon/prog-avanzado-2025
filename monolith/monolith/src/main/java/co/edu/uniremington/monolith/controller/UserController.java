package co.edu.uniremington.monolith.controller;

import co.edu.uniremington.monolith.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    //base de datos facil
    private List<User> userlist= new ArrayList<>(List.of(
            new User(1l,"Juanito alimaña","juanito@email.com"),
            new User(2l,"Yurisa","jYurisa@email.com")

    ));

    @GetMapping
    public List<User> getUserlist() {
        return userlist;
    }
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userlist.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
