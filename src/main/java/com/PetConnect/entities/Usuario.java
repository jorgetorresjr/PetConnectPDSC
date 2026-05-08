package com.PetConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TB_USUARIO")
@SecondaryTable(
        name = "TB_FOTO_USUARIO",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "ID_USUARIO")
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DISC_USUARIO",
        discriminatorType = DiscriminatorType.STRING, length = 2)
public abstract class Usuario {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Valid
    @Embedded
    protected Endereco endereco = new Endereco();

    @NotBlank
    @Column(name = "TXT_TELEFONE")
    protected String telefone;

    @CPF
    @NotBlank
    @Column(name = "TXT_CPF")
    protected String cpf;

    @NotBlank
    @Column(name = "TXT_LOGIN")
    protected String login;

    @Pattern(regexp = "^[A-Z][a-z]+$", message = "{exemplo.jpa.Usuario.nome}")
    @NotBlank
    @Column(name = "TXT_NOME")
    protected String nome;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "IMG_FOTO", table = "TB_FOTO_USUARIO", nullable = true)
    private byte[] foto;

    @Email
    @NotBlank
    @Column(name = "TXT_EMAIL")
    protected String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$", message = "{exemplo.jpa.Usuario.senha}")
    @NotBlank
    @Column(name = "TXT_SENHA")
    protected String senha;

    @Past
    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_NASCIMENTO", nullable = true)
    protected Date dataNascimento;

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

}