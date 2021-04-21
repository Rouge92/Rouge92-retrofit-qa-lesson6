package Services;

import com.github.javafaker.Faker;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lesson6.db.dao.CategoriesMapper;
import lesson6.db.dao.ProductsMapper;
import lesson6.db.model.ProductsExample;
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
import ru.geekbrains.dto.Products;
import ru.geekbrains.service.ProductService;
import ru.geekbrains.utils.DbUtils;
import ru.geekbrains.utils.RetrofitUtils;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetProductTests {
    Integer productId;
    Faker faker = new Faker();
    static ProductService productService;
    Product product;

    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {

        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();
        RestAssured.filters(new AllureRestAssured());
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);

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
    void simpleGetProductTest() {
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

        retrofit2.Response<Product> response2 =
                productService.getProduct(productId)
                .execute();
        assertThat(response2.isSuccessful()).isTrue();
        assertThat(response2.code()).isEqualTo(200);
        assert response2.body() != null;
        assertThat(response2.body().getTitle()).isEqualTo(product.getTitle());
        assertThat(response2.body().getPrice()).isEqualTo(product.getPrice());
        assertThat(response2.body().getCategoryTitle()).isEqualTo(product.getCategoryTitle());


    }

    @SneakyThrows
    @Test
    void getProductNegativeTest() {
        retrofit2.Response<Product> response =
        productService.getProduct(890)
                .execute();
        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find product with id: 890");
        }

    }


    //должен упасть
    @SneakyThrows
    @Test
    void getProductsTest() {
        retrofit2.Response<Products> response2 =
                productService.getProducts()
                        .execute();
        assertThat(response2.isSuccessful()).isTrue();
        assert response2.body() != null;
        int responseSize = response2.body().getProducts().size();
        ProductsExample example = new ProductsExample();
        int bdSize = productsMapper.selectByExample(example).size();
            assertThat(responseSize == bdSize);
    }



    @AfterEach
    void tearDown() {
        DbUtils.getCategoriesMapper().deleteByPrimaryKey(productId);
    }

}
