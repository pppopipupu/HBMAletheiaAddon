#version 120

uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float time;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;

void main() {
    float t = (iTime > 0.0) ? iTime : time;

    if (u_hasFbo == 1) {
        vec2 uv = gl_TexCoord[0].st;
        vec2 center = vec2(0.5, 0.5);
        vec2 toCenter = uv - center;
        float dist = length(toCenter);

        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        float radius = 1.35;
        vec2 refractionOffset = vec2(0.0);

        if (dist < radius) {
            float force = pow((radius - dist) / radius, 2.2) * 0.18;
            refractionOffset = -normalize(toCenter) * force;
        }

        vec2 sampleCoord = clamp(screenUv + refractionOffset, vec2(0.001), vec2(0.999));
        vec3 background = texture2D(u_screenTexture, sampleCoord).rgb;

        gl_FragColor = vec4(background, 1.0);
    } else {
        discard;
    }
}
