package com.muamalat.springboot.controller;


import com.muamalat.springboot.entity.Product;
import com.muamalat.springboot.pojo.ReqRes;
import com.muamalat.springboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class AdminUsers {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/public/product")
    public ResponseEntity<Object> getAllProduct(){
        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping("/admin/saveproduct")
    public ResponseEntity<Object> saveProduct(@RequestBody ReqRes productRequest){
        Product productToSave = new Product();
        productToSave.setName(productRequest.getName());
        return ResponseEntity.ok(productRepository.save(productToSave));
    }

    @GetMapping("/user/alone")
    public ResponseEntity<Object> userAlone(){
        return ResponseEntity.ok("Users alone can access this api only");
    }

    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminAndUserApi(){
        return ResponseEntity.ok("Both Admin and Users can access this api only");
    }

    /** You can use this to get the details(name,email,role,ip, e.t.c) of user accessing the service*/
    @GetMapping("/public/email")
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication); //get all details(name,email,password,roles e.t.c) of the user
        System.out.println(authentication.getDetails()); // get remote ip
        System.out.println(authentication.getName()); //returns the email because the email is the unique identifier
        return authentication.getName(); // returns the email
    }

    @GetMapping("/adminuser/profilepicture")
    public ResponseEntity<Object> profilePicture(@RequestParam String filename){
        try {
            Path root = Paths.get("uploads");
//            Path file = root.resolve(filename);
            Resource resource = new UrlResource(root.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
