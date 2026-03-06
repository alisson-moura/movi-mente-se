package com.funfarme.movimente_se;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

@ActiveProfiles("test")
@SpringBootTest()
@AutoConfigureRestTestClient
public class BaseE2ETest {
    @MockitoBean
    protected Clock clock;

    @Autowired
    protected RestTestClient client;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    @BeforeEach
    void limparBancoDeDados() {
        jdbcTemplate.execute("TRUNCATE TABLE sessoes RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE alunos");
        jdbcTemplate.execute("TRUNCATE TABLE grupos RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE empresas RESTART IDENTITY CASCADE");

        jdbcTemplate.update("""
                INSERT INTO
                    empresas (nome, cnpj)
                VALUES
                    (?, NULL)
                """, faker.company().name());

        jdbcTemplate.update("""
                INSERT INTO
                    grupos (nome, descricao, pagante)
                VALUES
                    (?, ?, TRUE)
                """, faker.commerce().department(), faker.lorem().sentence());

        Mockito.when(this.clock.instant()).thenReturn(Instant.now());
        Mockito.when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());
    }
}