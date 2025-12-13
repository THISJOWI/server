package uk.thisjowi.OTP.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.thisjowi.OTP.util.EncryptionUtil;

@Converter
public class IntegerCryptoConverter implements AttributeConverter<Integer, String> {

    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        return attribute == null ? null : EncryptionUtil.encrypt(String.valueOf(attribute));
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Integer.valueOf(EncryptionUtil.decrypt(dbData));
    }
}
