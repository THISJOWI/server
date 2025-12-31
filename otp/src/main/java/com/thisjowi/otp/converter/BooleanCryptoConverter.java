package com.thisjowi.otp.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.thisjowi.otp.util.EncryptionUtil;

@Converter
public class BooleanCryptoConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return attribute == null ? null : EncryptionUtil.encrypt(String.valueOf(attribute));
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Boolean.valueOf(EncryptionUtil.decrypt(dbData));
    }
}
