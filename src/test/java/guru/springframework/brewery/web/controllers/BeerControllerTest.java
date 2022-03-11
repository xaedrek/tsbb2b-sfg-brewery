package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    @Mock
    BeerService beerService;

    @InjectMocks
    BeerController beerController;

    MockMvc mockMvc;

    BeerDto validBeer;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder().id(UUID.randomUUID())
                .version(1)
                .beerName("Mahou")
                .beerStyle(BeerStyleEnum.IPA)
                .price(new BigDecimal(12.99))
                .quantityOnHand(4)
                .upc(12342354256L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(beerController).build();
    }

    @Test
    void listBeers() {
    }

    @Test
    void getBeerById() throws Exception {
        given(beerService.findBeerById(any())).willReturn(validBeer);
        mockMvc.perform(get("/api/v1/beer/"+validBeer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id",is(validBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName",is("Mahou")));
    }

    @DisplayName("List Ops -")
    @Nested
    public class TestListOperations{
            @Captor
        ArgumentCaptor<String> beerNameCaptor;
        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setUp() {
            List<BeerDto> beers = new ArrayList<>();
            beers.add(validBeer);
            beers.add(BeerDto.builder().id(UUID.randomUUID())
                    .version(1)
                    .beerName("San Miguel")
                    .beerStyle(BeerStyleEnum.IPA)
                    .price(new BigDecimal(12.99))
                    .quantityOnHand(4)
                    .upc(12342354256L)
                    .createdDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now())
                    .build());
            beerPagedList = new BeerPagedList(beers,PageRequest.of(1,1), 2L);
            given(beerService.listBeers(beerNameCaptor.capture(),beerStyleEnumCaptor.capture(),pageRequestCaptor.capture()))
                    .willReturn(beerPagedList);
        }

        @Test
        void  testListBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content",hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id",is(validBeer.getId().toString())));
        }

    }
}