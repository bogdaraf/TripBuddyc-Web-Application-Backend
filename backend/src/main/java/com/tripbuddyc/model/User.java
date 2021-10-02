package com.tripbuddyc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tripbuddyc.config.db.IntegerListConverter;
import com.tripbuddyc.config.db.StringListConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @JsonIgnore
    private String username;

    @Email
    @NotBlank
    @JsonIgnore
    private String email;

    @NotBlank
    @JsonIgnore
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String birthDate;

    @NotBlank
    private Integer age;

    @NotBlank
    private String gender;

    @NotBlank
    private String country;

    @Convert(converter = StringListConverter.class)
    @Column(name = "languages", length = 1024)
    @NotBlank
    private List<String> languages;

    @NotBlank
    private String description;

    private String picType;

    @Column(name = "picByte", length = 1000000)
    private byte[] picByte;

    @Convert(converter = IntegerListConverter.class)
    @NotBlank
    @JsonIgnore
    private List<Integer> chatsIds;

    private Integer connectedChatId = null;

    @NotBlank
    @JsonIgnore
    private String enabled;

    @NotBlank
    @JsonIgnore
    private String authorities;

    @NotBlank
    @JsonIgnore
    private String accountNonLocked;

    @NotBlank
    @JsonIgnore
    private String accountNonExpired;

    @NotBlank
    @JsonIgnore
    private String credentialsNonExpired;


    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String firstName, String lastName, String birthDate, String gender,
                String country, List<String> languages, String description) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.age = calculateAge(this.birthDate);
        this.gender = gender;
        this.country = country;
        this.languages = languages;
        this.description = description;
        this.chatsIds = new ArrayList<>();
    }

    /*public User(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }*/

    /*public static JwtUserDetails build(User user) {
        return new JwtUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword());
    }*/

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer calculateAge(String birthDate) {
        String temp = birthDate.replace('.', '-');
        String[] dateArray = temp.split("-");

        int day = Integer.parseInt(dateArray[0]);
        int month = Integer.parseInt(dateArray[1]);
        int year = Integer.parseInt(dateArray[2]);

        LocalDate currentDate = LocalDate.now();
        int currDay = currentDate.getDayOfMonth();
        int currMonth = currentDate.getMonthValue();
        int currYear = currentDate.getYear();

        if(currMonth < month || currMonth == month && currDay < day) {
            currYear--;
        }

        return currYear - year;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void addLanguages(List<String> languages) {
        this.languages.addAll(languages);
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    public byte[] getPicByte() {
        return picByte;
    }

    public void setPicByte(byte[] picByte) {
        this.picByte = picByte;
    }

    public void addChatId(Integer chatId) {
        chatsIds.add(chatId);
    }

    public void removeChatId(Integer chatId) {
        chatsIds.remove(chatId);
    }

    public List<Integer> getChatsIds() {
        return chatsIds;
    }

    public void setChatsIds(List<Integer> chatsIds) {
        this.chatsIds = chatsIds;
    }

    public Integer getConnectedChatId() {
        return connectedChatId;
    }

    public void setConnectedChatId(Integer connectedChatId) {
        this.connectedChatId = connectedChatId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
}
