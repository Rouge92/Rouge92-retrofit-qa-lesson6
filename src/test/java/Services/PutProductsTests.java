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

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;

public class PutProductsTests {

    private static Product productUpd;

    Integer productId;
    private static Faker faker = new Faker();
    static ProductService productService;
    Product product;
    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;
    static ProductsMapper productsMapper2;
    static CategoriesMapper categoriesMapper2;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        productsMapper = DbUtils.getProductsMapper();
        categoriesMapper = DbUtils.getCategoriesMapper();

        productsMapper2 = DbUtils.getProductsMapper();
        categoriesMapper2 = DbUtils.getCategoriesMapper();

        RestAssured.filters(new AllureRestAssured());

        productService = RetrofitUtils
                .getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withCategoryTitle(CategoryType.FOOD.getTitle())
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());

    }


    @SneakyThrows
    @Test
    void updateAllProductFuelsTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withCategoryTitle(CategoryType.ELECTRONICS.getTitle())
                .withPrice((int)(Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(productUpd.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(productUpd.getPrice());
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
                ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateTitleProductFuelsTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle(response.body().getCategoryTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isNotEqualTo(product.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(0);
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateLongTitleProductFuelsTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle((faker.lorem().fixedString(100)))
                .withCategoryTitle(response.body().getCategoryTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isNotEqualTo(product.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(0);
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updatePriceProductFuelsTest() {
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
        productUpd  = new Product()
                .withId(productId)
                .withPrice(456)
                .withCategoryTitle(response.body().getCategoryTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(productUpd.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(productUpd.getPrice());
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateCategoryProductFuelsTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle(response.body().getTitle())
                .withPrice(response.body().getPrice())
                .withCategoryTitle(CategoryType.ELECTRONICS.getTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(productUpd.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(productUpd.getPrice());
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateWithEmptyFuelsTest() {
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
        productUpd = new Product()
                .withId(productId);
        retrofit2.Response<Product> response2 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response2.isSuccessful()).isFalse();
        assertThat(response2.code()).isEqualTo(500);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).contains("Internal Server Error");
        }
    }

    @SneakyThrows
    @Test
    void updateByNonexistentIdProductTest() {
        productUpd = new Product()
                .withId(1234)
                .withCategoryTitle(CategoryType.ELECTRONICS.getTitle())
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(faker.food().ingredient());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isFalse();
        assertThat(response3.code()).isEqualTo(400);
        if (response3 != null && !response3.isSuccessful() && response3.errorBody() != null) {
            ResponseBody body = response3.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getStatus().equals(400));
        }
    }

    @SneakyThrows
    @Test
    void updateEmptyTitleProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withPrice((int) (Math.random() * 1000 + 1))
                .withCategoryTitle(CategoryType.ELECTRONICS.getTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(null);
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(productUpd.getPrice());
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateEmptyPriceProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle(faker.food().ingredient())
                .withCategoryTitle(CategoryType.ELECTRONICS.getTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(productUpd.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(0);
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateEmptyCategoryProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle(faker.food().ingredient())
                .withPrice((int) (Math.random() * 1000 + 1));
        retrofit2.Response<Product> response2 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response2.isSuccessful()).isFalse();
        assertThat(response2.code()).isEqualTo(500);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).contains("Internal Server Error");

        }
    }

    @SneakyThrows
    @Test
    void updateOnlyTitleProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle(faker.food().ingredient());
        retrofit2.Response<Product> response2 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response2.isSuccessful()).isFalse();
        assertThat(response2.code()).isEqualTo(500);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).contains("Internal Server Error");

        }
    }
    @SneakyThrows
    @Test
    void updateOnlyPriceProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withPrice((int) (Math.random() * 1000 + 1));
        retrofit2.Response<Product> response2 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response2.isSuccessful()).isFalse();
        assertThat(response2.code()).isEqualTo(500);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).contains("Internal Server Error");

        }
    }

    @SneakyThrows
    @Test
    void updateOnlyCategoryProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withCategoryTitle(CategoryType.ELECTRONICS.getTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(null);
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(0);
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void updateOnlyLongTitleProductTest() {
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
        productUpd = new Product()
                .withId(productId)
                .withTitle((faker.lorem().fixedString(100)));
        retrofit2.Response<Product> response2 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response2.isSuccessful()).isFalse();
        assertThat(response2.code()).isEqualTo(500);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).contains("Internal Server Error");

        }
    }

    @SneakyThrows
    @Test
    void updateWithFractalPriceFuelsTest() {
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
        productUpd  = new Product()
                .withId(productId)
                .withPrice(456.50)
                .withCategoryTitle(response.body().getCategoryTitle());
        retrofit2.Response<Product> response3 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response3.isSuccessful()).isTrue();
        assert response3.body() != null;
        assert productId != null;
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(productUpd.getTitle());
        assertThat(productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(456);
        assertThat(categoriesMapper2.selectByPrimaryKey(
                productsMapper2.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(productUpd.getCategoryTitle());

    }

    @SneakyThrows
    @Test
    void updateWithInvalidPriceFuelsTest() {
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
        productUpd  = new Product()
                .withId(productId)
                .withPrice("4hghjfnds")
                .withCategoryTitle(response.body().getCategoryTitle());
        retrofit2.Response<Product> response2 =
                productService.updateProduct(productUpd)
                        .execute();
        assertThat(response2.isSuccessful()).isFalse();
        assertThat(response2.code()).isEqualTo(400);
        if (response2 != null && !response2.isSuccessful() && response2.errorBody() != null) {
            ResponseBody body = response2.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).contains("Bad Request");
        }
    }


    @AfterEach
    void tearDown() {
        DbUtils.getCategoriesMapper().deleteByPrimaryKey(productId);
    }
}
