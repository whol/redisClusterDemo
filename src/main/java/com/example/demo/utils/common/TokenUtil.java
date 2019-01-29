package com.example.demo.utils.common;

import com.example.demo.common.utils.Constants;
import com.example.demo.cache.service.CacheServiceFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.UUID;

@Slf4j
public class TokenUtil {
    private TokenUtil() {

    }

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private static Claims getAllClaimsFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(getTokenSecret()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
            log.error("获取token信息失败", e);
        }
        return claims;
    }

    /**
     * 获取token密钥
     * @return
     */
    private static String getTokenSecret() {
        String tokenSecret = null;
        if (StringUtils.isEmpty(tokenSecret)) {
            tokenSecret = Constants.TOKEN_SECRET;
        }
        return tokenSecret;
    }

    /**
     * 更新token
     * @param token
     * @return
     */
    public static String refreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (null == claims) {
            return null;
        }
        claims.setIssuedAt(new Date());
        String refreshedToken = Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, getTokenSecret()).compact();
        try {
            CacheServiceFactory.getService().setex(claims.getIssuer(), token, getExpiredInSecond());
        } catch (Exception e) {
            log.error("存储redis数据异常", e);
        }
        return refreshedToken;
    }

    /**
     * 生成全局token
     * @return
     */
    public static String generateToken() {
        String uuid = Constants.USER_CACHE_FLAG.TOKEN + UUID.randomUUID().toString()
                .replace("-", "");
        String tokenSecret = getTokenSecret();
        String token = Jwts.builder().setId("").setIssuer(uuid).setSubject("")
                .setAudience("").setIssuedAt(new Date()).setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, tokenSecret).compact();
        //缓存token信息
        try {
            CacheServiceFactory.getService().setex(uuid, token, getExpiredInSecond());
        } catch (Exception e) {
            log.error("存储redis数据异常", e);
        }
        return token;
    }

    private static int getExpiredInSecond() {
        return Constants.TOKEN_EXPIRES_TIME * 60;
    }

    private static Date generateExpirationDate() {
        return new Date((new Date()).getTime() + getExpiredIn());
    }

    private static int getExpiredIn() {
        return Constants.TOKEN_EXPIRES_TIME * 60 * 1000;
    }

    /**
     * 删除token
     * @param token
     */
    public static void deleteToken(String token) {
        String uuid;
        if (StringUtils.isNotEmpty(token)) {
            uuid = getIssuerFromToken(token);
            try {
                CacheServiceFactory.getService().decr(uuid);
            } catch (Exception e) {
                log.error("存储redis数据异常", e);
            }
        }
    }

    public static String getIdFromToken(String token) {
        String id;
        try {
            Claims claims = getAllClaimsFromToken(token);
            id = claims.getId();
        } catch (Exception e) {
            id = null;
            log.error("从token里获取不到USER_ID", e);
        }
        return id;
    }

    /**
     * 获取发行人，对应UUID
     * @param token
     * @return
     */
    public static String getIssuerFromToken(String token) {
        String issuer;
        try {
            Claims claims = getAllClaimsFromToken(token);
            issuer = claims.getIssuer();
        } catch (Exception e) {
            issuer = null;
            log.error("从token里获取不到UUID", e);
        }
        return issuer;
    }

    /**
     * 获取token主题
     * @param token
     * @return
     */
    public static String getSubjectFromToken(String token) {
        String subject;
        try {
            Claims claims = getAllClaimsFromToken(token);
            subject = claims.getSubject();
        } catch (Exception e) {
            subject = null;
            log.error("从token里获取不到主题", e);
        }
        return subject;
    }

    /**
     * 获取开始时间
     * @param token
     * @return
     */
    public static Date getIssuedDateFromToken(String token) {
        Date issueAt;
        try {
            Claims claims = getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
            log.error("从token里获取不到开始时间", e);
        }
        return issueAt;
    }

    /**
     * 获取到期时间
     * @param token
     * @return
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            Claims claims = getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
            log.error("从token里获取不到到期时间", e);
        }
        return expiration;
    }

    /**
     * 获取接收人
     * @param token
     * @return
     */
    public static String getAudienceFromToken(String token) {
        String audience;
        try {
            Claims claims = getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (Exception e) {
            audience = null;
            log.error("从token里获取不到接收人", e);
        }
        return audience;
    }

    /**
     * 登录用户生成token
     * @param userId
     * @param accessToken
     * @return
     */
    public static String generateLoginToken(String userId, String accessToken) {
        String uuid = null;
        if (StringUtils.isNotEmpty(accessToken)) {
            uuid = getIssuerFromToken(accessToken);
        }
        if (StringUtils.isEmpty(uuid)) {
            uuid = Constants.USER_CACHE_FLAG.TOKEN + UUID.randomUUID().toString()
                    .replace("-", "");
        }
        String tokenSecret = getTokenSecret();
        String token = Jwts.builder().setId(userId).setIssuer(uuid).setSubject("")
                .setAudience("").setIssuedAt(new Date()).setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, tokenSecret).compact();
        //缓存token信息
        try {
            CacheServiceFactory.getService().setex(uuid, token, getExpiredInSecond());
        } catch (Exception e) {
            log.error("存储redis数据异常", e);
        }
        return token;
    }

    /**
     * 在token里获取对应参数的值
     * @param param
     * @param token
     * @return
     */
    public static String getUserInfoClaimsFromToken(String param, String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (null == claims) {
            return "";
        }
        if (claims.containsKey(param)) {
            return claims.get(param).toString();
        }
        return "";
    }

    /**
     * 校验传送来的token和缓存的token是否一致
     * @param token
     * @return
     */
    public static boolean verifyToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (null == claims) {
            return false;
        }
        String issuer = claims.getIssuer();
        String cacheToken;
        try {
            cacheToken = CacheServiceFactory.getService().getString(issuer);
            //校验通过后修改token超时时间
            CacheServiceFactory.getService().expire(issuer, getExpiredInSecond());
        } catch (Exception e) {
            cacheToken = null;
            log.error("获取不到存储的token", e);
        }
        return StringUtils.equals(token, cacheToken);
    }


}
