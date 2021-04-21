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

public class PostProductTests {
    private static Product emptyTitleProduct;
    private static Product emptyPriceProduct;
    private static Product maxIntProduct;
    private static Product emptyCategoryProduct;
    private static Product negPriceProduct;
    private static Product emptyFullProduct;
    private static Product longTitleProduct;
    private static Product onlyLongTitleProduct;
    private static Product onlyPriceProduct;
    private static Product onlyCategoryProduct;
    private static Product fracPriceProduct;
    private static Product invalidPriceProduct;

    Integer productId;
    private static Faker faker = new Faker();
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

        productService = RetrofitUtils
                .getRetrofit()
                .create(ProductService.class);

        emptyTitleProduct = new Product()
                .withPrice(0)
                .withCategoryTitle("Food");

        emptyPriceProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food");

        maxIntProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withPrice(2147483647)
                .withCategoryTitle("Food");

        emptyCategoryProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withPrice(100)
                .withCategoryTitle(null);

        negPriceProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withPrice(-450)
                .withCategoryTitle("Food");

        emptyFullProduct = new Product();

        longTitleProduct = new Product()
                .withTitle(faker.lorem().fixedString(100))
                .withPrice(100)
                .withCategoryTitle("Food");

        onlyLongTitleProduct = new Product().
                withTitle(faker.lorem().fixedString(100));

        onlyPriceProduct = new Product()
                .withPrice(234);

        onlyCategoryProduct = new Product()
                .withCategoryTitle("Food");

//TODO доделать две проверки

        fracPriceProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withPrice(250.50)
                .withCategoryTitle("Food");

        invalidPriceProduct = new Product()
                .withTitle(faker.food().ingredient())
                .withPrice("4657fhgbvj")
                .withCategoryTitle("Food");
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
    void createNewProductTest() {
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

    }

    @SneakyThrows
    @Test
    void createNewProductNegativeTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product.withId(555))
                        .execute();
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be null for new entity");
        }
    }

    @SneakyThrows
    @Test
    void createNewEmptyTitleProductTest() {

        retrofit2.Response<Product> response =
                productService.createProduct(emptyTitleProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(emptyTitleProduct.getTitle());
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(emptyTitleProduct.getPrice());
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(emptyTitleProduct.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void createNewEmptyPriceProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(emptyPriceProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(emptyPriceProduct.getTitle());
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(0);
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(emptyPriceProduct.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void createMaxINTPriceProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(maxIntProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(maxIntProduct.getTitle());
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(maxIntProduct.getPrice());
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(maxIntProduct.getCategoryTitle());

    }

    //Этот тест должен успасть
    @SneakyThrows
    @Test
    void createNegativePriceProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(negPriceProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Bad Request");
        }
    }

    @SneakyThrows
    @Test
    void createNewEmptyCategoryProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(emptyCategoryProduct)
                        .execute();
        assertThat(response.code()).isEqualTo(500);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Internal Server Error");
        }
    }

    @SneakyThrows
    @Test
    void createNewFullEmptyCProductTest() {

        retrofit2.Response<Product> response =
                productService.createProduct(emptyFullProduct)
                        .execute();
        assertThat(response.code()).isEqualTo(500);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Internal Server Error");
        }
    }

    @SneakyThrows
    @Test
    void createNewProductLongTitleTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(longTitleProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assertThat(response.isSuccessful()).isTrue();
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(longTitleProduct.getTitle());
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(longTitleProduct.getPrice());
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(longTitleProduct.getCategoryTitle());
    }


    @SneakyThrows
    @Test
    void createNewOnlyTitleProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(onlyLongTitleProduct)
                        .execute();
        assertThat(response.code()).isEqualTo(500);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Internal Server Error");
        }
    }

    @SneakyThrows
    @Test
    void createNewOnlyPriceProductTest() {

        retrofit2.Response<Product> response =
                productService.createProduct(onlyPriceProduct)
                        .execute();
        assertThat(response.code()).isEqualTo(500);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getError()).isEqualTo("Internal Server Error");
        }
    }

    @SneakyThrows
    @Test
    void createOnlyCategoryProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(onlyCategoryProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(null);
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(0);
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(onlyCategoryProduct.getCategoryTitle());
    }

    @SneakyThrows
    @Test
    void createWithFractalPriceFuelsTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(fracPriceProduct)
                        .execute();
        productId = response.body().getId();
        assertThat(response.code()).isEqualTo(201);
        assert response.body() != null;
        assert productId != null;
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getTitle()).isEqualTo(fracPriceProduct.getTitle());
        assertThat(productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getPrice()).isEqualTo(250);
        assertThat(categoriesMapper.selectByPrimaryKey(
                productsMapper.selectByPrimaryKey(Long.valueOf(productId)).getCategory_id().intValue()
        ).getTitle()).isEqualTo(fracPriceProduct.getCategoryTitle());

    }

    @SneakyThrows
    @Test
    void createWithInvalidPriceFuelsTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(invalidPriceProduct)
                        .execute();
        assertThat(response.isSuccessful()).isFalse();
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
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
