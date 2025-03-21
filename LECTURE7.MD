Предыдущее занятие |         &nbsp;          | Следующее занятие
:----------------:|:-----------------------:|:----------------:
[Урок 10](LESSON10.MD) | [Содержание](../README.MD) | [Урок 12](LESSON11.md)

# Лекция 7. Тестовое окружение и покрытие кода

# Оглавление

1. [Введение в тему](#введение-в-тему)
2. [Тестовое окружение](#тестовое-окружение)
   * [Что такое окружение](#что-такое-окружение)
   * [Тестовое окружение](#тестовое-окружение-1)
   * [Подготовка тестового окружения](#подготовка-тестового-окружения)

---

<details>

<summary>Тестовое окружение</summary>

# Тестовое окружение

[Оглавление](#оглавление)

Современные приложения — это миллиарды строчек кода, которые описывают сложную систему со строгой и развитой логикой. 
Некоторые компании за время своего существования произвели и теперь 
поддерживают огромные базы рабочего кода. И чем больше разработчиков вносят изменения в продукт, 
тем выше становится вероятность появления в нём багов.

## Что такое окружение

**Среда, или окружение** (англ. _environment_) — это сервер или группа серверов, на которых находится копия приложения. Конкретное окружение часто называется **стендом** (англ. _stand_).

У любой компании есть как минимум одна среда — та, с которой взаимодействуют её клиенты. 
Но часто для полноценной работы над продуктом выделяют и другие окружения:

* **Среда разработки** (англ. _development environment_), **dev** или **dev-стенд**. 
Используется исключительно для разработки. Именно сюда стекаются изменения от всех программистов, 
работающих над проектом. Здесь же запускаются тесты, написанные разработчиками, и ставятся эксперименты.
* **Тестовая среда** (англ. _test environment_), **test** или **test-стенд**. Здесь продукт стабилизируется при помощи более сложных тестов. 
Например, **интеграционных** (англ. _integration tests_) **— которые объединяют (интегрируют) несколько программ/сервисов вместе. 
При интеграционном тестировании часто проверяют, корректно ли данные из одного сервиса передаются в другой. 
Такие тесты часто пишут отдельные команды тестировщиков.
* **Продуктовая среда** (англ. _production environment_), или **prod**. Её ещё называют «боевая среда» 
как противоположность тестовой, «учебной». 
С ней взаимодействуют клиенты компании, поэтому любые ошибки могут привести к денежным и репутационным потерям.
В идеальном мире к моменту выкладки кода на прод в программе уже не должно быть багов, влияющих на пользователя.

![img.png](img.png)

Многие компании создают и другие среды для своих целей. Например, одни воспроизводят баги в специфичном окружении — на сервере с определённой версией Java. 
Другие используют отдельные среды для разных видов тестов. 
Третьи вообще создают отдельное тестовое окружение для каждого изменения в коде. 

Во всём этом многообразии одна вещь остаётся верной всегда. Тесты сокращают количество багов, которые увидит конечный пользователь. 

> 💡 Окружением часто называют не только полноценный набор 
> серверов с копией приложения, но также и набор внешних параметров, от которых зависит работа программы. Это может быть и версия Java, и другие приложения, 
> и разные файлы конфигурации. Часть таких внешних параметров 
> можно контролировать во время тестирования, с чем тоже помогает библиотека JUnit.


## Тестовое окружение

При тестировании часто бывает удобно выделить набор доступных классов и конфигураций в мини-окружение,
чтобы использовать его для тестов. 
Такой подход позволяет упростить тестирование классов со сложным порядком инициализации. 

Возьмём в качестве примера `OvercomplicatedCookieFactory` (англ. «переусложнённая фабрика печенек») — класс по производству печенек с предсказаниями. 
При создании его экземпляра нужно указать:
* возможные позитивные и негативные предсказания,
* а также флаг, который будет говорить, какие нужно создавать печеньки: с позитивными предсказаниями или с негативными.

Каждая созданная печенька увеличивает внутренний счётчик готовых печенек. Давайте протестируем этот класс.

```java
import java.util.ArrayList;
import java.util.Random;

public class OvercomplicatedCookieFactory {
    private int cookiesCreated = 0;
    private final ArrayList<String> positiveTexts;
    private final ArrayList<String> negativeTexts;
    private final boolean isPositive;

    // класс Random используется, чтобы возвращать случайное предсказание из списка.
    private final Random rnd = new Random();

    public OvercomplicatedCookieFactory(
            ArrayList<String> positiveTexts,
            ArrayList<String> negativeTexts,
            boolean isPositive
    ) {
        this.positiveTexts = positiveTexts;
        this.negativeTexts = negativeTexts;
        this.isPositive = isPositive;
    }

    // Печём печеньку!
    public String bakeFortuneCookie() {
        StringBuilder cookieBuilder = new StringBuilder();

        // увеличиваем счётчик печенек:
        this.incrementNumberOfCookiesCreated();

        // возвращаем хорошее или плохое предсказание:
        if (isPositive) {
            cookieBuilder.append(randomPositive());
        } else {
            cookieBuilder.append(randomNegative());
        }

        // преобразовываем результат в строку:
        return cookieBuilder.toString();
    }

    // возвращаем количество испечённых печенек:
    public int getCookiesCreated() {
        return this.cookiesCreated;
    }

    // обнуляем счётчик созданных печенек:
    public void resetCookiesCreated() {
        this.cookiesCreated = 0;
    }

    // Увеличиваем счётчик испечённых печенек:
    private void incrementNumberOfCookiesCreated() {
        this.cookiesCreated++;
    }

    // Выбираем произвольное предсказание из списка позитивных:
    private String randomPositive() {
        // Получаем целое случайное число от нуля до максимального размера массива - 1.
        int randomIndex = rnd.nextInt(positiveTexts.size());
        return positiveTexts.get(randomIndex);
    }

    // Выбираем произвольное предсказание из списка негативных:
    private String randomNegative() {
        // Получаем целое случайное число от нуля до максимального размера массива - 1.
        int randomIndex = rnd.nextInt(negativeTexts.size());
        return negativeTexts.get(randomIndex);
    }
}
```

Первые два необходимых теста — создание позитивных предсказаний `shouldReturnPositiveCookie() `
и счётчика печенек `shouldIncreaseCounterByOneAfterCookieBaked()`. 

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class OvercomplicatedCookieFactoryTest {

    @Test
    public void shouldReturnPositiveCookie() {
        ArrayList<String> positiveTexts = new ArrayList<>();
        positiveTexts.add("Вам повезёт!");

        ArrayList<String> negativeTexts = new ArrayList<>();
        negativeTexts.add("Сегодня будет дождь");
        
                OvercomplicatedCookieFactory cookieFactory = new OvercomplicatedCookieFactory(
                positiveTexts,
                negativeTexts,
                true
        );
        String cookieText = cookieFactory.bakeFortuneCookie();
        Assertions.assertEquals("Вам повезёт!", cookieText);
    }

    @Test
    public void shouldIncreaseCounterByOneAfterCookieBaked() {
        ArrayList<String> positiveTexts = new ArrayList<>();
        positiveTexts.add("Вам повезёт!");

        ArrayList<String> negativeTexts = new ArrayList<>();
        negativeTexts.add("Сегодня будет дождь");
        
        OvercomplicatedCookieFactory cookieFactory = new OvercomplicatedCookieFactory(
                positiveTexts,
                negativeTexts,
                true
        );
        cookieFactory.bakeFortuneCookie();
        Assertions.assertEquals(1, cookieFactory.getCookiesCreated());
    }
}
```

Оба теста используют один и тот же код для создания экземпляра `OvercomplicatedCookieFactory`.
В данном случае это, скорее всего, не приведёт к проблемам. Но в реальных кодовых базах одни классы могут зависеть от других,
и такой код будет занимать очень много места. 


Один из вариантов решения этой проблемы — вынести экземпляр класса в переменную перед тестами.

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class OvercomplicatedCookieFactoryTest {
        private final static ArrayList<String> positiveTexts = new ArrayList<>();
    private final static ArrayList<String> negativeTexts = new ArrayList<>();
        
    private final static OvercomplicatedCookieFactory cookieFactory = new OvercomplicatedCookieFactory(
            positiveTexts,
            negativeTexts,
            true
    );

    @Test
    public void shouldReturnPositiveCookie() {
                negativeTexts.add("Сегодня будет дождь");
                positiveTexts.add("Вам повезёт!");
        String cookieText = cookieFactory.bakeFortuneCookie();
        Assertions.assertEquals("Вам повезёт!", cookieText);
    }

    @Test
    public void shouldIncreaseCounterByOneAfterCookieBaked() {
                negativeTexts.add("Сегодня будет дождь");
                positiveTexts.add("Вам повезёт!");
        cookieFactory.bakeFortuneCookie();
        Assertions.assertEquals(1, cookieFactory.getCookiesCreated());
    }
}

```

В этом случае переменной cookieFactory будет присвоен экземпляр `OvercomplicatedCookieFactory`, 
который используется в обоих тестах. Всё бы хорошо, но таким образом в тестовом классе 
появится нежелательная зависимость между тестами. Дело в том, что порядок их запуска в общем случае не определён.
Более того, он может меняться от запуска к запуску.

Предположим, что сначала выполнится тест создания позитивных предсказаний `shouldReturnPositiveCookie()`.
Поскольку в нём вызывается метод `bakeFortuneCookie()`, внутренний счётчик переменной `cookieFactory` увеличится на единицу. 
Если после этого сразу запустится тест счётчика печенек `shouldIncreaseCounterByOneAfterCookieBaked()`, 
то к внутреннему значению прибавиться ещё единица, и `assertEquals()` выдаст ошибку. Если же сначала запустится тест
счётчика `shouldIncreaseCounterByOneAfterCookieBaked()`, а затем `shouldReturnPositiveCookie()`, 
такой ошибки не произойдёт.
Подобные баги очень сложно обнаружить и исправить, 
поэтому тестовое окружение лучше готовить под каждый тест по отдельности.


## Подготовка тестового окружения

В `JUnit` есть четыре специальных аннотации, которые позволяют запускать определённый код до или после тестов. 

Первая из них — `@BeforeEach` (англ. «перед каждым»). Если установить её над методом, этот метод будет запускаться всякий раз перед любым из тестов внутри класса. 

Исправленный код будет выглядеть следующим образом:

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OvercomplicatedCookieFactoryTest {

    private static OvercomplicatedCookieFactory cookieFactory;

    @BeforeEach
    public void beforeEach() {
        ArrayList<String> positiveTexts = new ArrayList<>();
        positiveTexts.add("Вам повезёт!");

        ArrayList<String> negativeTexts = new ArrayList<>();
        negativeTexts.add("Сегодня будет дождь");
        
        cookieFactory = new OvercomplicatedCookieFactory(
                positiveTexts,
                negativeTexts,
                true
        );
    }

    @Test
    public void shouldReturnPositiveCookie() {
        String cookieText = cookieFactory.bakeFortuneCookie();
        Assertions.assertEquals("Вам повезёт!", cookieText);
    }

    @Test
    public void shouldIncreaseCounterByOneAfterCookieBaked() {
        cookieFactory.bakeFortuneCookie();
        Assertions.assertEquals(1, cookieFactory.getCookiesCreated());
    }
}
```

Теперь можно писать любое количество проверок. 
Поскольку перед каждым тестом создаётся новый экземпляр `OvercomplicatedCookieFactory`, 
тесты больше не будут зависеть друг от друга. 

Другие три аннотации позволяют запускать произвольный код:
* `@BeforeAll` (англ. «перед всеми») — один раз до запуска всех тестов;
* `@AfterEach` (англ. «после каждого») — каждый раз после окончания каждого теста;
* `@AfterAll `(англ. «после всех») — один раз после окончания всех тестов.

Аннотации, срабатывающие один раз: `@BeforeAll` и `@AfterAll`, должны стоять над статическим методом. 
Если метод будет не статичный, тест попросту не запустится.

Все четыре аннотации могут содержаться в одном тестовом классе. 
Они ведут себя так, как и ожидается: 
* перед началом тестов исполнится `@BeforeAll`, 
* перед каждым тестом — `@BeforeEach`, 
* после каждого — `@AfterEach`. 
* В самом конце, после выполнения всех тестов, сработает `@AfterAll`.

```java
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBenchExample {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Running beforeAll");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Running afterAll");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("--Running beforeEach");
    }

    @AfterEach
    void afterEach() {
        System.out.println("--Running afterEach");
    }


    @Test
    public void someTestOne() {
        System.out.println("-- --Running test 1");
    }

    @Test
    public void someTestTwo() {
        System.out.println("-- --Running test 2");
    }
}
```

</details>

---



Предыдущее занятие | &nbsp; | Следующее занятие
:----------------:|:----------:|:----------------:
[Урок ](LESSON10.md) | [Содержание](readme.md) | [Урок 12](LESSON12.md)