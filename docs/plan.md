# Migration Plan

## Goal
Migrate a Java EE 7 CoolStore Monolith application from javax.* package namespaces to jakarta.* package namespaces (Jakarta EE 9+).

## Source → Target
Java EE 7 (javax.*) → Jakarta EE 9+ (jakarta.*)

## Scope
- Files affected: 23
- Estimated complexity: Medium
- Hardest areas:
  1. Message-Driven Bean configuration (special handling for javax.jms.Topic string literals in @ActivationConfigProperty)
  2. Persistence layer (jakarta.persistence annotations are identical but namespace-dependent)
  3. JMS/Messaging patterns (multiple import statements across multiple files)

## Key Decisions Applied

1. **Dependency upgrade strategy**: Update pom.xml to use Jakarta EE 9 BOM/APIs rather than Java EE 7, ensuring all transitive dependencies resolve to jakarta.* packages.

2. **Literal string replacement in @ActivationConfigProperty**: The string value "javax.jms.Topic" in OrderServiceMDB.java needs to be updated to "jakarta.jms.Topic" to match the new namespace. While this is technically a non-code literal, it is configuration tied to the migration target.

3. **Weblogic lifecycle classes remain unchanged**: Files in `weblogic/application/` do not use javax.* imports and are not part of the Jakarta migration scope; they will be left as-is.

4. **No structural changes to application logic**: This migration only involves import statement and configuration literal updates. No method signatures, class hierarchies, or business logic are altered.

## Approach

**Phase 1: Build Configuration**
- Update pom.xml to depend on Jakarta EE 9+ libraries instead of Java EE 7
- Replace javax:javaee-* dependencies with jakarta.* equivalents

**Phase 2: Model Layer**
- Update all javax.persistence.* imports to jakarta.persistence.*
- Update all javax.xml.bind.annotation.* imports to jakarta.xml.bind.annotation.*
- Update all javax.enterprise.context.* imports to jakarta.enterprise.context.*

**Phase 3: Persistence and Utility Layers**
- Update all javax.enterprise.* imports (inject, context, spi) to jakarta.enterprise.*
- Update all javax.persistence.* and javax.sql.* imports to jakarta.persistence.* and jakarta.sql.*
- Update all javax.json.* imports to jakarta.json.*
- Update all javax.annotation.* imports to jakarta.annotation.*

**Phase 4: Service Layer (Business Logic)**
- Update all javax.ejb.* imports to jakarta.ejb.*
- Update all javax.jms.* imports to jakarta.jms.*
- Update all javax.inject.* and javax.enterprise.* imports to jakarta.inject.* and jakarta.enterprise.*
- Update all javax.naming.* imports to jakarta.naming.*
- Update all javax.rmi.* imports to jakarta.rmi.*
- **COMPLEX**: Update string literal "javax.jms.Topic" → "jakarta.jms.Topic" in OrderServiceMDB configuration

**Phase 5: REST/API Layer**
- Update all javax.ws.rs.* imports to jakarta.ws.rs.*
- Update all javax.enterprise.context.* imports to jakarta.enterprise.context.*
- Update all javax.inject.* imports to jakarta.inject.*

## Steps

### Step 1: Update pom.xml dependencies
- Phase: Build Configuration
- File: pom.xml
- Action: MODIFY
- What to do:
    - Remove: `<dependency><groupId>javax</groupId><artifactId>javaee-web-api</artifactId><version>7.0</version></dependency>`
    - Remove: `<dependency><groupId>javax</groupId><artifactId>javaee-api</artifactId><version>7.0</version></dependency>`
    - Add new dependencies for Jakarta EE 9:
      ```xml
      <dependency>
          <groupId>jakarta.platform</groupId>
          <artifactId>jakarta.jakartaee-api</artifactId>
          <version>9.1.0</version>
          <scope>provided</scope>
      </dependency>
      ```
    - Update javax.jms dependency: `<groupId>org.jboss.spec.javax.jms</groupId>` → use Jakarta equivalent from jakarta.jms or jboss spec
    - Update javax.rmi dependency: `<groupId>org.jboss.spec.javax.rmi</groupId>` → use Jakarta equivalent
- Why: Jakarta EE 9 uses jakarta.* packages; dependencies must provide those packages instead of javax.* packages
- Depends on: none
- Verify: `mvn clean compile` runs without "cannot find symbol" errors for jakarta imports

### Step 2: Migrate imports in CatalogItemEntity.java
- Phase: Model Layer
- File: src/main/java/com/redhat/coolstore/model/CatalogItemEntity.java
- Action: MODIFY
- What to do: Replace `import javax.persistence.*;` with `import jakarta.persistence.*;`
- Why: Persistence annotations moved to jakarta.persistence namespace in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.persistence imports remain; file compiles without errors

### Step 3: Migrate imports in InventoryEntity.java
- Phase: Model Layer
- File: src/main/java/com/redhat/coolstore/model/InventoryEntity.java
- Action: MODIFY
- What to do:
    - Replace `import javax.persistence.Column;` with `import jakarta.persistence.Column;`
    - Replace `import javax.persistence.Entity;` with `import jakarta.persistence.Entity;`
    - Replace `import javax.persistence.Id;` with `import jakarta.persistence.Id;`
    - Replace `import javax.persistence.Table;` with `import jakarta.persistence.Table;`
    - Replace `import javax.persistence.UniqueConstraint;` with `import jakarta.persistence.UniqueConstraint;`
    - Replace `import javax.xml.bind.annotation.XmlRootElement;` with `import jakarta.xml.bind.annotation.XmlRootElement;`
- Why: Persistence and JAXB annotations moved to jakarta.* namespaces in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.persistence or javax.xml.bind imports remain; file compiles without errors

### Step 4: Migrate imports in Order.java
- Phase: Model Layer
- File: src/main/java/com/redhat/coolstore/model/Order.java
- Action: MODIFY
- What to do: Replace all individual javax.persistence imports with jakarta.persistence equivalents:
    - `javax.persistence.CascadeType` → `jakarta.persistence.CascadeType`
    - `javax.persistence.Column` → `jakarta.persistence.Column`
    - `javax.persistence.Entity` → `jakarta.persistence.Entity`
    - `javax.persistence.FetchType` → `jakarta.persistence.FetchType`
    - `javax.persistence.GeneratedValue` → `jakarta.persistence.GeneratedValue`
    - `javax.persistence.Id` → `jakarta.persistence.Id`
    - `javax.persistence.JoinColumn` → `jakarta.persistence.JoinColumn`
    - `javax.persistence.OneToMany` → `jakarta.persistence.OneToMany`
    - `javax.persistence.Table` → `jakarta.persistence.Table`
- Why: All persistence annotations migrated to jakarta.persistence in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.persistence imports remain; file compiles without errors

### Step 5: Migrate imports in OrderItem.java
- Phase: Model Layer
- File: src/main/java/com/redhat/coolstore/model/OrderItem.java
- Action: MODIFY
- What to do: Replace all individual javax.persistence imports with jakarta.persistence equivalents:
    - `javax.persistence.Column` → `jakarta.persistence.Column`
    - `javax.persistence.Entity` → `jakarta.persistence.Entity`
    - `javax.persistence.GeneratedValue` → `jakarta.persistence.GeneratedValue`
    - `javax.persistence.Id` → `jakarta.persistence.Id`
    - `javax.persistence.Table` → `jakarta.persistence.Table`
- Why: Persistence annotations migrated to jakarta.persistence in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.persistence imports remain; file compiles without errors

### Step 6: Migrate imports in ShoppingCart.java
- Phase: Model Layer
- File: src/main/java/com/redhat/coolstore/model/ShoppingCart.java
- Action: MODIFY
- What to do: Replace `import javax.enterprise.context.Dependent;` with `import jakarta.enterprise.context.Dependent;`
- Why: Enterprise context annotations moved to jakarta.enterprise namespace in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.enterprise imports remain; file compiles without errors

### Step 7: COMPLEX - Migrate imports in Resources.java
- Phase: Persistence and Utility Layers
- File: src/main/java/com/redhat/coolstore/persistence/Resources.java
- Action: MODIFY
- What to do:
    - Replace `import javax.enterprise.context.Dependent;` with `import jakarta.enterprise.context.Dependent;`
    - Replace `import javax.enterprise.inject.Produces;` with `import jakarta.enterprise.inject.Produces;`
    - Replace `import javax.persistence.EntityManager;` with `import jakarta.persistence.EntityManager;`
    - Replace `import javax.persistence.PersistenceContext;` with `import jakarta.persistence.PersistenceContext;`
- Why: All enterprise and persistence imports moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: grep shows no javax.enterprise or javax.persistence imports; file compiles without errors

### Step 8: Migrate imports in CartEndpoint.java
- Phase: REST/API Layer
- File: src/main/java/com/redhat/coolstore/rest/CartEndpoint.java
- Action: MODIFY
- What to do:
    - Replace `import javax.enterprise.context.SessionScoped;` with `import jakarta.enterprise.context.SessionScoped;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.ws.rs.DELETE;` with `import jakarta.ws.rs.DELETE;`
    - Replace `import javax.ws.rs.GET;` with `import jakarta.ws.rs.GET;`
    - Replace `import javax.ws.rs.POST;` with `import jakarta.ws.rs.POST;`
    - Replace `import javax.ws.rs.Path;` with `import jakarta.ws.rs.Path;`
    - Replace `import javax.ws.rs.PathParam;` with `import jakarta.ws.rs.PathParam;`
    - Replace `import javax.ws.rs.Produces;` with `import jakarta.ws.rs.Produces;`
    - Replace `import javax.ws.rs.core.MediaType;` with `import jakarta.ws.rs.core.MediaType;`
- Why: JAX-RS and enterprise injection annotations moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.enterprise, javax.inject, or javax.ws.rs imports remain; file compiles without errors

### Step 9: Migrate imports in OrderEndpoint.java
- Phase: REST/API Layer
- File: src/main/java/com/redhat/coolstore/rest/OrderEndpoint.java
- Action: MODIFY
- What to do:
    - Replace `import javax.enterprise.context.RequestScoped;` with `import jakarta.enterprise.context.RequestScoped;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.ws.rs.Consumes;` with `import jakarta.ws.rs.Consumes;`
    - Replace `import javax.ws.rs.GET;` with `import jakarta.ws.rs.GET;`
    - Replace `import javax.ws.rs.Path;` with `import jakarta.ws.rs.Path;`
    - Replace `import javax.ws.rs.PathParam;` with `import jakarta.ws.rs.PathParam;`
    - Replace `import javax.ws.rs.Produces;` with `import jakarta.ws.rs.Produces;`
    - Replace `import javax.ws.rs.core.MediaType;` with `import jakarta.ws.rs.core.MediaType;`
- Why: JAX-RS and enterprise injection annotations moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.enterprise, javax.inject, or javax.ws.rs imports remain; file compiles without errors

### Step 10: Migrate imports in ProductEndpoint.java
- Phase: REST/API Layer
- File: src/main/java/com/redhat/coolstore/rest/ProductEndpoint.java
- Action: MODIFY
- What to do:
    - Replace `import javax.enterprise.context.RequestScoped;` with `import jakarta.enterprise.context.RequestScoped;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.ws.rs.*;` with `import jakarta.ws.rs.*;`
    - Replace `import javax.ws.rs.core.MediaType;` with `import jakarta.ws.rs.core.MediaType;`
- Why: JAX-RS and enterprise injection annotations moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.enterprise, javax.inject, or javax.ws.rs imports remain; file compiles without errors

### Step 11: Migrate imports in RestApplication.java
- Phase: REST/API Layer
- File: src/main/java/com/redhat/coolstore/rest/RestApplication.java
- Action: MODIFY
- What to do:
    - Replace `import javax.ws.rs.ApplicationPath;` with `import jakarta.ws.rs.ApplicationPath;`
    - Replace `import javax.ws.rs.core.Application;` with `import jakarta.ws.rs.core.Application;`
- Why: JAX-RS classes and annotations moved to jakarta.ws.rs namespace
- Depends on: Step 1
- Verify: No javax.ws.rs imports remain; file compiles without errors

### Step 12: Migrate imports in CatalogService.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/CatalogService.java
- Action: MODIFY
- What to do:
    - Replace `import javax.ejb.Stateless;` with `import jakarta.ejb.Stateless;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.persistence.EntityManager;` with `import jakarta.persistence.EntityManager;`
    - Replace `import javax.persistence.criteria.CriteriaBuilder;` with `import jakarta.persistence.criteria.CriteriaBuilder;`
    - Replace `import javax.persistence.criteria.CriteriaQuery;` with `import jakarta.persistence.criteria.CriteriaQuery;`
    - Replace `import javax.persistence.criteria.Root;` with `import jakarta.persistence.criteria.Root;`
- Why: EJB, injection, and persistence classes moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.ejb, javax.inject, or javax.persistence imports remain; file compiles without errors

### Step 13: COMPLEX - Migrate imports in InventoryNotificationMDB.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/InventoryNotificationMDB.java
- Action: MODIFY
- What to do:
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.jms.*;` with `import jakarta.jms.*;`
    - Replace `import javax.naming.Context;` with `import jakarta.naming.Context;`
    - Replace `import javax.naming.InitialContext;` with `import jakarta.naming.InitialContext;`
    - Replace `import javax.naming.NamingException;` with `import jakarta.naming.NamingException;`
    - Replace `import javax.rmi.PortableRemoteObject;` with `import jakarta.rmi.PortableRemoteObject;`
- Why: JMS, naming, RMI, and injection classes moved to jakarta.* namespaces in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.inject, javax.jms, javax.naming, or javax.rmi imports remain; file compiles without errors

### Step 14: Migrate imports in OrderService.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/OrderService.java
- Action: MODIFY
- What to do:
    - Replace `import javax.ejb.Stateless;` with `import jakarta.ejb.Stateless;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.persistence.EntityManager;` with `import jakarta.persistence.EntityManager;`
    - Replace `import javax.persistence.criteria.CriteriaBuilder;` with `import jakarta.persistence.criteria.CriteriaBuilder;`
    - Replace `import javax.persistence.criteria.CriteriaQuery;` with `import jakarta.persistence.criteria.CriteriaQuery;`
    - Replace `import javax.persistence.criteria.Root;` with `import jakarta.persistence.criteria.Root;`
- Why: EJB, injection, and persistence classes moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.ejb, javax.inject, or javax.persistence imports remain; file compiles without errors

### Step 15: COMPLEX - Migrate imports and configuration in OrderServiceMDB.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/OrderServiceMDB.java
- Action: MODIFY
- What to do:
    - Replace import statements:
        - `import javax.ejb.ActivationConfigProperty;` → `import jakarta.ejb.ActivationConfigProperty;`
        - `import javax.ejb.MessageDriven;` → `import jakarta.ejb.MessageDriven;`
        - `import javax.inject.Inject;` → `import jakarta.inject.Inject;`
        - `import javax.jms.JMSException;` → `import jakarta.jms.JMSException;`
        - `import javax.jms.Message;` → `import jakarta.jms.Message;`
        - `import javax.jms.MessageListener;` → `import jakarta.jms.MessageListener;`
        - `import javax.jms.TextMessage;` → `import jakarta.jms.TextMessage;`
    - Replace configuration string literal in @ActivationConfigProperty:
        - `propertyValue = "javax.jms.Topic"` → `propertyValue = "jakarta.jms.Topic"`
        (This appears in the destinationType property value within the @MessageDriven annotation)
- Why: All EJB, JMS, and injection classes moved to jakarta.* namespaces; the JMS class name literal must also update to match the Jakarta namespace
- Depends on: Step 1
- Verify: No javax.ejb or javax.jms imports remain; grep confirms no "javax.jms.Topic" strings remain; file compiles without errors

### Step 16: Migrate imports in ProductService.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/ProductService.java
- Action: MODIFY
- What to do:
    - Replace `import javax.ejb.Stateless;` with `import jakarta.ejb.Stateless;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
- Why: EJB and injection classes moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.ejb or javax.inject imports remain; file compiles without errors

### Step 17: Migrate imports in PromoService.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/PromoService.java
- Action: MODIFY
- What to do: Replace `import javax.enterprise.context.ApplicationScoped;` with `import jakarta.enterprise.context.ApplicationScoped;`
- Why: Enterprise context annotations moved to jakarta.enterprise namespace
- Depends on: Step 1
- Verify: No javax.enterprise imports remain; file compiles without errors

### Step 18: Migrate imports in ShippingService.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/ShippingService.java
- Action: MODIFY
- What to do:
    - Replace `import javax.ejb.Remote;` with `import jakarta.ejb.Remote;`
    - Replace `import javax.ejb.Stateless;` with `import jakarta.ejb.Stateless;`
- Why: EJB annotations moved to jakarta.ejb namespace
- Depends on: Step 1
- Verify: No javax.ejb imports remain; file compiles without errors

### Step 19: COMPLEX - Migrate imports in ShoppingCartOrderProcessor.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/ShoppingCartOrderProcessor.java
- Action: MODIFY
- What to do:
    - Replace `import javax.annotation.Resource;` with `import jakarta.annotation.Resource;`
    - Replace `import javax.ejb.Stateless;` with `import jakarta.ejb.Stateless;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.jms.JMSContext;` with `import jakarta.jms.JMSContext;`
    - Replace `import javax.jms.Topic;` with `import jakarta.jms.Topic;`
- Why: EJB, annotation, JMS, and injection classes moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.annotation, javax.ejb, javax.inject, or javax.jms imports remain; file compiles without errors

### Step 20: COMPLEX - Migrate imports in ShoppingCartService.java
- Phase: Service Layer
- File: src/main/java/com/redhat/coolstore/service/ShoppingCartService.java
- Action: MODIFY
- What to do:
    - Replace `import javax.ejb.Stateful;` with `import jakarta.ejb.Stateful;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.naming.Context;` with `import jakarta.naming.Context;`
    - Replace `import javax.naming.InitialContext;` with `import jakarta.naming.InitialContext;`
    - Replace `import javax.naming.NamingException;` with `import jakarta.naming.NamingException;`
- Why: EJB, injection, and naming classes moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.ejb, javax.inject, or javax.naming imports remain; file compiles without errors

### Step 21: Migrate imports in DataBaseMigrationStartup.java
- Phase: Persistence and Utility Layers
- File: src/main/java/com/redhat/coolstore/utils/DataBaseMigrationStartup.java
- Action: MODIFY
- What to do:
    - Replace `import javax.annotation.PostConstruct;` with `import jakarta.annotation.PostConstruct;`
    - Replace `import javax.annotation.Resource;` with `import jakarta.annotation.Resource;`
    - Replace `import javax.ejb.Singleton;` with `import jakarta.ejb.Singleton;`
    - Replace `import javax.ejb.Startup;` with `import jakarta.ejb.Startup;`
    - Replace `import javax.ejb.TransactionManagement;` with `import jakarta.ejb.TransactionManagement;`
    - Replace `import javax.ejb.TransactionManagementType;` with `import jakarta.ejb.TransactionManagementType;`
    - Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
    - Replace `import javax.sql.DataSource;` with `import jakarta.sql.DataSource;`
- Why: All annotation, EJB, injection, and data source classes moved to jakarta.* namespaces
- Depends on: Step 1
- Verify: No javax.annotation, javax.ejb, javax.inject, or javax.sql imports remain; file compiles without errors

### Step 22: Migrate imports in Producers.java
- Phase: Persistence and Utility Layers
- File: src/main/java/com/redhat/coolstore/utils/Producers.java
- Action: MODIFY
- What to do:
    - Replace `import javax.enterprise.inject.Produces;` with `import jakarta.enterprise.inject.Produces;`
    - Replace `import javax.enterprise.inject.spi.InjectionPoint;` with `import jakarta.enterprise.inject.spi.InjectionPoint;`
- Why: Enterprise injection classes moved to jakarta.enterprise namespace
- Depends on: Step 1
- Verify: No javax.enterprise imports remain; file compiles without errors

### Step 23: Migrate imports in StartupListener.java
- Phase: Persistence and Utility Layers
- File: src/main/java/com/redhat/coolstore/utils/StartupListener.java
- Action: MODIFY
- What to do: Replace `import javax.inject.Inject;` with `import jakarta.inject.Inject;`
- Why: Injection classes moved to jakarta.inject namespace
- Depends on: Step 1
- Verify: No javax.inject imports remain; file compiles without errors

### Step 24: Migrate imports in Transformers.java
- Phase: Persistence and Utility Layers
- File: src/main/java/com/redhat/coolstore/utils/Transformers.java
- Action: MODIFY
- What to do:
    - Replace `import javax.json.Json;` with `import jakarta.json.Json;`
    - Replace `import javax.json.JsonArray;` with `import jakarta.json.JsonArray;`
    - Replace `import javax.json.JsonArrayBuilder;` with `import jakarta.json.JsonArrayBuilder;`
    - Replace `import javax.json.JsonObject;` with `import jakarta.json.JsonObject;`
    - Replace `import javax.json.JsonReader;` with `import jakarta.json.JsonReader;`
    - Replace `import javax.json.JsonWriter;` with `import jakarta.json.JsonWriter;`
- Why: JSON-Processing (JSON-P) API moved to jakarta.json namespace in Jakarta EE 9
- Depends on: Step 1
- Verify: No javax.json imports remain; file compiles without errors

## Verification

- **Build**: `mvn clean compile` — should compile without any "cannot find symbol" errors related to javax or jakarta packages
- **Test**: The project has `<maven.test.skip>true</maven.test.skip>` in pom.xml, so no unit tests are configured. Manual testing against JBoss EAP or compatible Jakarta EE 9+ application server is required.
- **Blackbox**: Deploy the WAR to a Jakarta EE 9+ compatible application server (e.g., WildFly 27+, JBoss EAP 8.0+). Verify that:
  - Application starts without javax namespace resolution errors
  - JAX-RS endpoints respond to HTTP requests (GET /products, GET /cart, POST /order, etc.)
  - Database persistence works (entities load/save correctly)
  - JMS message processing works (message-driven beans receive and process messages)
  - Dependency injection works (all @Inject fields are populated)

## Notes

- **No JEE server configuration changes needed**: The application code migration is purely about import statements and one configuration string literal. The target Jakarta EE 9+ server (WildFly 27+, JBoss EAP 8.0+) will provide all jakarta.* APIs automatically.
- **Flyway database migration is unaffected**: The dependency on flyway-core does not use javax or jakarta packages and requires no changes.
- **Web deployment descriptor**: No web.xml file is present in this project; the application uses annotation-based configuration (@WebServlet, @EJB, @MessageDriven, etc.).
- **Weblogic lifecycle code**: The weblogic/application/ classes are helper classes that do not depend on javax.* or jakarta.* packages and do not require migration.
- **PostgreSQL JDBC module**: The README references a custom module.xml for org.postgresql with dependencies on javax.api and javax.transaction.api. After migration to Jakarta EE 9+, this module.xml must be updated to reference jakarta equivalents. This is server-side configuration, not code, and is documented in the README but not in the pom.xml.
