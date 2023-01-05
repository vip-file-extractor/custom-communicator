package com.evam.marketing.communication.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Data utils
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Component
@Slf4j
public class DbUtil {
    private static final int CAPACITY = 100000;
    private static final String SMS = "sms";

    private final AtomicReference<BigInteger> currentCallback = new AtomicReference<>(BigInteger.valueOf(0));
    private final AtomicReference<BigInteger> limitCallback = new AtomicReference<>(BigInteger.valueOf(-1));
    private final AtomicReference<BigInteger> currentSms = new AtomicReference<>(BigInteger.valueOf(0));
    private final AtomicReference<BigInteger> limitSms = new AtomicReference<>(BigInteger.valueOf(-1));
    private final JdbcTemplate jdbcTemplate;
    private final boolean isDialectOracle;

    public DbUtil(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        final Session session = (Session) entityManager.getDelegate();
        final SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
        final Dialect dialect = sessionFactory.getJdbcServices().getDialect();
        isDialectOracle = dialect instanceof Oracle8iDialect;
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getNextSmsSequence() {
        return getNextSequence("sms").longValue();
    }

    public long getNextCallbackSequence() {
        return getNextSequence("callback").longValue();
    }

    private BigInteger getNextSequence(String name) {
        if (SMS.equals(name)) {
            return getNextSequenceInternal(name, currentSms, limitSms);
        } else {
            return getNextSequenceInternal(name, currentCallback, limitCallback);
        }
    }

    private BigInteger getNextSequenceInternal(String name, AtomicReference<BigInteger> currentSms,
        AtomicReference<BigInteger> limitSms) {
        while (true) {
            BigInteger origVal = currentSms.get();
            if (origVal.compareTo(limitSms.get()) >= 0) {
                getNextSequenceBatch(name);
            }
            BigInteger newVal = origVal.add(BigInteger.ONE);
            if (currentSms.compareAndSet(origVal, newVal)) {
                return newVal;
            }
        }
    }

    private synchronized void getNextSequenceBatch(String name) {
        if (SMS.equals(name)) {
            getNextSequenceBatchInternal(name, currentSms, limitSms);
        } else {
            getNextSequenceBatchInternal(name, currentCallback, limitCallback);
        }
    }

    private void getNextSequenceBatchInternal(
            String name,
            AtomicReference<BigInteger> current,
            AtomicReference<BigInteger> limit
    ) {
        if (current.get().compareTo(limit.get()) >= 0) {
            final BigDecimal sequence = jdbcTemplate.queryForObject(getNextSequenceSql(name), BigDecimal.class);
            BigInteger bigInteger = sequence.toBigInteger();

            BigInteger to = bigInteger.multiply(BigInteger.valueOf(CAPACITY));
            limit.set(to);
            current.set(to.subtract(BigInteger.valueOf(CAPACITY)));
            log.debug("Sequence value for {}[{}], current[{}], limit[{}]",
                new String[] {name, bigInteger.toString(), current.get().toString(), limit.get().toString()});
        }
    }


    private String getNextSequenceSql(String name) {
        if (SMS.equals(name)) {
            if (isDialectOracle) {
                return "SELECT jazz_custom_sms_sequence.NEXTVAL FROM DUAL";
            } else {
                return "SELECT NEXTVAL('jazz_custom_sms_sequence')";
            }
        } else {
            if (isDialectOracle) {
                return "SELECT jazz_custom_comm_smsc_cbck_seq.NEXTVAL FROM DUAL";
            } else {
                return "SELECT NEXTVAL('jazz_custom_comm_smsc_cbck_seq')";
            }
        }
    }
}
