package Services;

import com.github.javafaker.Faker;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lesson6.db.dao.CategoriesMapper;
import lesson6.db.dao.ProductsMapper;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Converter;
import ru.geekbrains.Parts.CategoryType;
import ru.geekbrains.dto.ErrorBody;
import ru.geekbrains.dto.Product;
import ru.geekbrains.service.ProductService;
import ru.geekbrains.utils.DbUtils;
import ru.geekbrains.utils.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteProductTests {
    Integer productId;
    Faker faker = new Faker();
    static ProductService productService;
    Product product;
    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;


    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        RestAssured.filters(new AllureRestAssured());
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();

    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withCategoryTitle(CategoryType.FOOD.getTitle())
                .withPrice((int)(Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());

    }

    @SneakyThrows
    @Test
    void simpleDeleteProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(product.getTitle());
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(product.getPrice());
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(product.getCategoryTitle());
        productService.deleteProduct(productId)
                .execute();
        assertThat(response.isSuccessful()).isTrue();
        retrofit2.Response<Product> response2 =
                productService.getProduct(productId)
                        .execute();
        assertThat(response2.code()).isEqualTo(404);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).contains("Unable to find product with id: ");

        }
    }
    @SneakyThrows
    @Test
    void deleteNonExistentProductTest() {
        productService.deleteProduct(000)
                .execute();
        retrofit2.Response<Product> response2 =
                productService.getProduct(000)
                        .execute();
        assertThat(response2.code()).isEqualTo(404);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).contains("Unable to find product with id: ");

        }
    }

}
