package com.thisjowi.otp.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.thisjowi.otp.util.EncryptionUtil;

@Converter
public class LongCryptoConverter implements AttributeConverter<Long, String> {

    @Override
    public String convertToDatabaseColumn(Long attribute) {
        return attribute == null ? null : EncryptionUtil.encrypt(String.valueOf(attribute));
    }

    @Override
    public Long convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Long.valueOf(EncryptionUtil.decrypt(dbData));
    }
}
